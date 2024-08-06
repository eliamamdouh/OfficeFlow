// requestController.js
const { db } = require('../firebase-init');
const jwt = require('jsonwebtoken');
require('dotenv').config();

const getLocationForDate = (schedule, date) => {
    let location = null;
    Object.keys(schedule).forEach(week => {
        schedule[week].forEach(day => {
            if (day.day === date) {
                location = day.location;
            }
        });
    });
    return location;
};

const submitRequest = async (req, res) => {
    try {
        const { authorization } = req.headers;
        const { newDate, dayToChange } = req.body;

        if (!authorization ) {
            return res.status(400).json({ message: 'Authorization header is missing' });
        }
        console.log("Headers received:", req.headers);

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
        const usersCollectionRef = db.collection('Users');
        const userDoc = await usersCollectionRef.doc(userId).get();
        if (!userDoc.exists) {
            return res.status(404).json({ message: 'User not found' });
        }

         if (newDate === dayToChange) {
            return res.status(400).json({ message: 'The new date and the day to change cannot be the same' });
        }
        const userData = userDoc.data();
        const schedule = userData.schedule;

        const locationForDayToChange = getLocationForDate(schedule, dayToChange);
        const locationForNewDate = getLocationForDate(schedule, newDate);

        if (locationForDayToChange === locationForNewDate) {
            return res.status(400).json({ message: 'The selected days must be from different locations (one office, one home)' });
        }

        // Create a new request
        const requestsCollectionRef = db.collection('Requests');
        const requestDocRef = requestsCollectionRef.doc(); 

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
