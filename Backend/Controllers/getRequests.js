// viewRequestsController.js
const { db } = require('../firebase-init.js');
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
require('dotenv').config();
const { StatusCodes } = require ("http-status-codes");
 
const getRequests = async (req, res) => {
    console.log('gowa viewreq')
    try {
        console.log('gowa viewreq')
        const { authorization } = req.headers;
 
        if (!authorization) {
            return res.status(StatusCodes.BAD_REQUEST).json({ message: 'Authorization header is missing' });
        }
 
        const token = authorization.split(' ')[1];
        if (!token) {
            return res.status(StatusCodes.UNAUTHORIZED).json({ message: 'No token provided' });
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

        console.log('Decoded Token:', decoded);
        const userId = decoded.userId;
        console.log('UserID:', userId);
 
        // Fetch the user's requests from the database
        const requestsCollectionRef = db.collection('Requests');
        const userRequestsQuery = await requestsCollectionRef.where('userId', '==', userId).get();
 
        if (userRequestsQuery.empty) {
            return res.status(StatusCodes.NOT_FOUND).json({ message: 'No requests found for this user' });
        }
 
        const userRequests = [];
        userRequestsQuery.forEach(doc => {
            userRequests.push({ id: doc.id, ...doc.data() });
        });
        return res.status(StatusCodes.OK).json(userRequests);
    } catch (error) {
        console.error("Error retrieving user requests:", error);
        //console.log("dhdbss")
        res.status(StatusCodes.INTERNAL_SERVER_ERROR).json({ message: 'Server error', error: error.message });
    }
};
 
module.exports = { getRequests };