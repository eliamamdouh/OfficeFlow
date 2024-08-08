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
        const { newDate, dayToChange, reason } = req.body;

        if (!authorization) {
            return res.status(400).json({ message: 'Authorization header is missing' });
        }
        console.log("Headers received:", req.headers);

        if (!newDate || !dayToChange || !reason) {
            return res.status(400).json({ message: 'New date, day to change, and reason are required' });
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

        const userData = userDoc.data();
        console.log("User data retrieved:", userData);

        if (newDate === dayToChange) {
            return res.status(400).json({ message: 'The new date and the day to change cannot be the same' });
        }

        const schedule = userData.schedule;

        const locationForDayToChange = getLocationForDate(schedule, dayToChange);
        const locationForNewDate = getLocationForDate(schedule, newDate);

        if (locationForDayToChange === locationForNewDate) {
            return res.status(400).json({ message: 'The selected days must be from different locations (one office, one home)' });
        }

        // Ensure the managerName is present, otherwise handle the missing value
        const managerName = userData.managerName || 'Unknown Manager';

        // Create a new request
        const requestsCollectionRef = db.collection('Requests');
        const requestDocRef = requestsCollectionRef.doc();

        // Ensure the data object is properly formatted and contains valid values
        const requestData = {
            userId,
            managerName,
            dayToChange,
            newDate,
            reason, // Save the reason in the database
            status: 'Pending',
            requestDate: new Date().toISOString(),
        };

        // Check for any undefined or null values in requestData
        for (const key in requestData) {
            if (requestData[key] === undefined || requestData[key] === null) {
                console.error(`Invalid data for ${key}:`, requestData[key]);
                return res.status(400).json({ message: `Invalid data for ${key}` });
            }
        }

        console.log("Request data being saved:", requestData);

        await requestDocRef.set(requestData);

        res.status(201).json({ message: 'Request submitted successfully', requestId: requestDocRef.id });
    } catch (error) {
        console.error("Error submitting request:", error);
        res.status(500).json({ message: 'Server error', error: error.message });
    }
};

module.exports = { submitRequest };