const { db } = require('../firebase-init');
const { StatusCodes } = require("http-status-codes");
const jwt = require('jsonwebtoken');
require("dotenv").config();


const viewSchedule = async (req, res) => {
    try {
        const { authorization } = req.headers;

        if (!authorization) {
            return res.status(StatusCodes.BAD_REQUEST).send('Authorization token is required');
        }
        console.log('Headers received:', req.headers);

        const token = authorization.split(' ')[1];
        const decodedToken = jwt.verify(token, process.env.JWT_SECRET);
        // Extract the userId from the decoded token
        const userId = decodedToken.userId; 
        console.log('Authorization Header:', authorization);


        const usersCollectionRef = db.collection('Users');
        const userDoc = await usersCollectionRef.doc(userId).get();

        if (!userDoc.exists) {
            return res.status(StatusCodes.NOT_FOUND).send('User not found');
        }

        const userData = userDoc.data();
        const schedule = userData.schedule;

        res.status(StatusCodes.OK).json({ message: 'Schedule retrieved successfully', schedule });
    } catch (error) {
        console.error("Error viewing schedule:", error);
        res.status(StatusCodes.INTERNAL_SERVER_ERROR).send('Server error');
    }
};


module.exports = { viewSchedule };
