const { db } = require('../firebase-init.js');
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
require('dotenv').config();
const { StatusCodes } = require("http-status-codes");
const moment = require('moment');

const viewRequests = async (req, res) => {
    try {
        const { authorization } = req.headers;

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

        // Fetch the user's requests from the database
        const requestsCollectionRef = db.collection('Requests');
        const userRequestsQuery = await requestsCollectionRef.where('userId', '==', userId).get();

        if (userRequestsQuery.empty) {
            return res.status(404).json({ message: 'No requests found for this user' });
        }

        const userRequests = [];
        userRequestsQuery.forEach(doc => {
            const data = doc.data();
            
            // Parse dates using moment, assuming requestDate is a string
            const requestDate = moment(data.requestDate);
            const timeAgo = requestDate.fromNow();
            
            const request = {
                id: doc.id,
                timeAgo: timeAgo,
                description: `Change ${data.dayToChange} to ${data.newDate}\nReason: ${data.reason}`,
                status: data.status,
            };
            
            userRequests.push(request);
        });

        return res.status(200).json(userRequests);
    } catch (error) {
        console.error("Error retrieving user requests:", error);
        res.status(500).json({ message: 'Server error', error: error.message });
    }
};

module.exports = { viewRequests };
