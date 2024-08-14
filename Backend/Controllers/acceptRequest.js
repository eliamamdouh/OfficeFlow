const { db } = require('../firebase-init');
const jwt = require('jsonwebtoken');
require('dotenv').config();

const acceptRequest = async (req, res) => {
    try {
        const { authorization } = req.headers;
        const { requestId } = req.body;

        console.log("Engy: " + authorization)

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

        const decoded = jwt.verify(token, process.env.JWT_SECRET_KEY);
        const userId = decoded.userId;

        // Update the request status to "Accepted"
        const requestsCollectionRef = db.collection('Requests');
        const requestDocRef = requestsCollectionRef.doc(requestId);

        const requestDoc = await requestDocRef.get();
        if (!requestDoc.exists) {
            return res.status(404).json({ message: 'Request not found' });
        }

        const requestData = requestDoc.data();
        if (requestData.status !== 'Pending') {
            return res.status(400).json({ message: 'Request status is not Pending' });
        }

        await requestDocRef.update({ status: 'Accepted' });

        res.status(200).json({ message: 'Request accepted successfully' });
    } catch (error) {
        console.error('Error accepting request:', error);
        res.status(500).json({ message: 'Server error', error: error.message });
    }
};

module.exports = { acceptRequest };
