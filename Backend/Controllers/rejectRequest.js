const { db, sendNotification } = require('../firebase-init');
const jwt = require('jsonwebtoken');
require('dotenv').config();

const rejectRequest = async (req, res) => {
    try {
        const { authorization } = req.headers;
        const { requestId } = req.body;

        console.log("Authorization: " + authorization);

        if (!authorization) {
            return res.status(400).json('Authorization header is missing');
        }
        
        if (!requestId) {
            return res.status(400).json('Request ID is required');
        }

        const token = authorization.split(' ')[1];
        if (!token) {
            return res.status(401).json({ message: 'No token provided' });
        }

        let decoded;
        try {
            decoded = jwt.verify(token, process.env.JWT_SECRET_KEY);
        } catch (error) {
            if (error.name === 'TokenExpiredError') {
                return res.status(401).json({ message: 'Token expired' });
            }
            throw error; // Re-throw other errors
        }
        const userId = decoded.userId;

        // Update the request status to "Accepted"
        const requestsCollectionRef = db.collection('Requests');
        const requestDocRef = requestsCollectionRef.doc(requestId);

        const requestDoc = await requestDocRef.get();
        if (!requestDoc.exists) {
            return res.status(404).json({ message: 'Request not found' });
        }

        const requestData = requestDoc.data();
        const requestedId = requestData.userId;
        const userDoc = await db.collection('Users').doc(requestedId).get();
        const userData = userDoc.data();
        if (requestData.status !== 'Pending') {
            return res.status(400).json({ message: 'Request status is not Pending' });
        }

        await requestDocRef.update({ status: 'Rejected' });

        const userToken = userData.deviceToken;
        if (userToken) {

            const messageTitle = 'Request Rejected';
            const messageBody = 'Your request has been Rejected.';
            await sendNotification(userToken, messageTitle, messageBody);
            console.log('Notification sent successfully');

            const notificationText = `${messageTitle}: ${messageBody}`;
            const notificationData = {
                userId: requestedId,
                text: notificationText,
                timestamp: new Date().toISOString()
            };

            await db.collection('Notifications').add(notificationData);
            console.log('Notification saved to database');
        }

        res.status(200).json({ message: 'Request Rejected successfully' });
    } catch (error) {
        console.error('Error rejecting request:', error);
        res.status(500).json({ message: 'Server error', error: error.message });
    }
};

module.exports = { rejectRequest };
