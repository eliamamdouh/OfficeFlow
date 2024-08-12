const { db } = require('../firebase-init.js');
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

        const decoded = jwt.verify(token, process.env.JWT_SECRET_KEY);
        const userId = decoded.userId;

        // Fetch the request document
        const requestDocRef = db.collection('Requests').doc(requestId);
        const requestDoc = await requestDocRef.get();

        if (!requestDoc.exists) {
            return res.status(404).json({ message: 'Request not found' });
        }

        const requestData = requestDoc.data();

        // Check if the request belongs to the user
        if (requestData.userId !== userId) {
            return res.status(403).json({ message: 'You do not have permission to cancel this request' });
        }

        // Remove the request from the database
        await requestDocRef.delete();

        return res.status(200).json({ message: 'Request successfully cancelled' });
    } catch (error) {
        console.error("Error cancelling request:", error);
        res.status(500).json({ message: 'Server error', error: error.message });
    }
};

module.exports = { cancelRequest };
