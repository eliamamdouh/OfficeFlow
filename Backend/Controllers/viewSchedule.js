const { STATUS_CODES } = require('http');
const { db } = require('../firebase-init');
const { StatusCodes } = require ("http-status-codes");

const viewSchedule = async (req, res) => {
    try {
        const { username } = req.body; 

        if (!username) {
            return res.status(StatusCodes.BAD_REQUEST).json({ message: 'Username is required' });
        }

        
        const usersCollectionRef = db.collection('users');
        const userQuery = await usersCollectionRef.where('username', '==', username).get();

        if (userQuery.empty) {
            return res.status(StatusCodes.NOT_FOUND).json({ message: 'User not found' });
        }

        // Retrieve the user's schedule
        const userDoc = userQuery.docs[0];
        const userData = userDoc.data();

        const schedule = userData.schedule;

        res.status(StatusCodes.OK).json({ message: 'Schedule retrieved successfully', schedule });
    } catch (error) {
        console.error("Error viewing schedule:", error);
        res.status(StatusCodes.INTERNAL_SERVER_ERROR).json({ message: 'Server error', error: error.message });
    }
};

module.exports = { viewSchedule };
