const { db ,sendNotification} = require('../firebase-init.js');
const jwt = require('jsonwebtoken');
require('dotenv').config();
const { StatusCodes } = require("http-status-codes");

const cancelRequest = async (req, res) => {
    try {
        const { authorization } = req.headers;
        const { requestId } = req.body;

        if (!authorization) {
            return res.status(400).json({ message: 'Authorization header is missing' });
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

        // Fetch the request document
        const requestDocRef = db.collection('Requests').doc(requestId);
        const requestDoc = await requestDocRef.get();

        if (!requestDoc.exists) {
            return res.status(404).json({ message: 'Request not found' });
        }

        const requestData = requestDoc.data();

        const requestedId =requestData.userId;
        const userDoc = await db.collection('Users').doc(requestedId).get();
        const userData = userDoc.data();
        // Check if the request belongs to the user
        if (requestData.userId !== userId) {
            return res.status(403).json({ message: 'You do not have permission to cancel this request' });
        }

        await requestDocRef.delete();
        const userToken = userData.deviceToken;
        if (userToken) {
            const messageTitle = 'Request Rejected';
            const messageBody = 'Your request has been Rejected.';
            await sendNotification(userToken, messageTitle, messageBody);
            console.log('Notification sent successfully');
        }
        return res.status(200).json({ message: 'Request successfully cancelled' });
    } catch (error) {
        console.error("Error cancelling request:", error);
        res.status(500).json({ message: 'Server error', error: error.message });
    }
};

module.exports = { cancelRequest };
