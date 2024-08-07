const { db } = require('../firebase-init');
const { StatusCodes } = require("http-status-codes");

const viewSchedule = async (req, res) => {
    try {
        const { userId } = req.query;

        if (!userId) {
            return res.status(StatusCodes.BAD_REQUEST).json({ message: 'User ID is required' });
        }

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
