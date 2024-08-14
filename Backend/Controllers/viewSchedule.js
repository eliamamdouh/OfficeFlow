const { db } = require('../firebase-init');
const { StatusCodes } = require("http-status-codes");
const jwt = require('jsonwebtoken');

const viewSchedule = async (req, res) => {
    try {

        const { authorization } = req.headers;
 
        if (!authorization) {
            return res.status(StatusCodes.BAD_REQUEST).json({ message: 'Authorization header is missing' });
        }
 
        const token = authorization.split(' ')[1];
        if (!token) {
            return res.status(StatusCodes.UNAUTHORIZED).json({ message: 'No token provided' });
        }
 
        const decoded = jwt.verify(token, process.env.JWT_SECRET_KEY);
        console.log('Decoded Token:', decoded);
        const userId = decoded.userId;
        console.log('UserID:', userId);
 
        // const { userId } = req.query;

        // if (!userId) {
        //     return res.status(StatusCodes.BAD_REQUEST).json({ message: 'User ID is required' });
        // }

        const usersCollectionRef = db.collection('Users');
        const userDoc = await usersCollectionRef.doc(userId).get();

        if (!userDoc.exists) {
            return res.status(StatusCodes.NOT_FOUND).json({ message: 'User not found' });
        }

        const userData = userDoc.data();
        const schedule = userData.schedule;

        res.status(StatusCodes.OK).json({ message: 'Schedule retrieved successfully', schedule });
    } catch (error) {
        console.error("Error viewing schedule:", error);
        res.status(StatusCodes.INTERNAL_SERVER_ERROR).json({ message: 'Server error', error: error.message });
    }
};

module.exports = { viewSchedule };
