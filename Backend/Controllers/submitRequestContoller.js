// requestController.js
const { db } = require('../firebase-init');
const jwt = require('jsonwebtoken');
require('dotenv').config();

const submitRequest = async (req, res) => {
    try {
        const { authorization } = req.headers;
        const { newDate, dayToChange } = req.body;

        if (!authorization ) {
            return res.status(400).json({ message: 'Authorization header is missing' });
        }
        if (!newDate || !dayToChange) {
            return res.status(400).json({ message: 'New date, and day to change are required' });
        }

        // Extract the JWT token
        const token = authorization.split(' ')[1];
        if (!token) {
            return res.status(401).json({ message: 'No token provided' });
        }

        // Verify and decode the token
        const decoded = jwt.verify(token, process.env.JWT_SECRET_KEY);
        const userId = decoded.userId;

        // Fetch the user data from the database
        const usersCollectionRef = db.collection('users');
        const userDoc = await usersCollectionRef.doc(userId).get();
        if (!userDoc.exists) {
            return res.status(404).json({ message: 'User not found' });
        }
        const userData = userDoc.data();

        // Create a new request
        const requestsCollectionRef = db.collection('Requests');
        const requestDocRef = requestsCollectionRef.doc(); // Auto-generate a document ID

        await requestDocRef.set({
            userId,
            managerName: userData.managerName,
            dayToChange,
            newDate,
            status: 'Pending',
            requestDate: new Date().toISOString(),
        });

        res.status(201).json({ message: 'Request submitted successfully', requestId: requestDocRef.id });
    } catch (error) {
        console.error("Error submitting request:", error);
        res.status(500).json({ message: 'Server error', error: error.message });
    }
};

module.exports = { submitRequest };
