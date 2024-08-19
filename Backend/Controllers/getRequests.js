const { db } = require('../firebase-init.js');
const jwt = require('jsonwebtoken');
require('dotenv').config();
const { StatusCodes } = require("http-status-codes");
const moment = require('moment');
const getRequests = async (req, res) => {
    try {
        const {authorization } = req.headers;
        if (!authorization) {
            return res.status(StatusCodes.BAD_REQUEST).send('Authorization header is missing');
        }
        const token = authorization.split(' ')[1];
        if (!token) {
            return res.status(StatusCodes.UNAUTHORIZED).send('No token provided');
        }
        let decoded;
        try {
            decoded = jwt.verify(token, process.env.JWT_SECRET_KEY);
        } catch (error) {
            if (error.name === 'TokenExpiredError') {
                return res.status(401).json({
                    message: 'Token expired'
                });
            }
            throw error; // Re-throw other errors
        }
        const managerId = decoded.userId;
        // Get manager's data
        const managerDoc = await db.collection('Users').doc(managerId).get();
        if (!managerDoc.exists) {
            return res.status(StatusCodes.NOT_FOUND).send('Manager not found');
        }
        const managerData = managerDoc.data();
        // Check if the user is a Super Manager
        if (managerData.username === 'SuperManager' || managerData.email === 'SuperManager@Deloitte.com') {
            // Fetch all requests without filtering
            const allRequestsQuery = await db.collection('Requests').get();
            if (allRequestsQuery.empty) {
                return res.status(StatusCodes.NOT_FOUND).send('No requests found');
            }
            const allRequests = [];
            for (const doc of allRequestsQuery.docs) {
                const data = doc.data();
                const requestDate = moment(data.requestDate);
                const timeAgo = requestDate.fromNow();
                // Fetch the user document to get the user's Fullname
                const userDoc = await db.collection('Users').doc(data.userId).get();
                const userFullname = userDoc.exists ? userDoc.data().Fullname : 'Unknown User';
                const request = {
                    id: doc.id,
                    timeAgo: timeAgo,
                    description: `Change ${data.dayToChange} to ${data.newDate}\nReason: ${data.reason}`,
                    userName: userFullname,
                    status: data.status,
                };
                allRequests.push(request);
            }
            return res.status(StatusCodes.OK).json(allRequests);
        } else {
            // Get manager's project ID
            const projectId = managerData.projectId;
            // Get requests related to the manager's project
            const requestsCollectionRef = db.collection('Requests');
            const userRequestsQuery = await requestsCollectionRef.where('projectId', '==', projectId).get();
            if (userRequestsQuery.empty) {
                return res.status(StatusCodes.NOT_FOUND).send('No requests found for this project');
            }
            const userRequests = [];
            for (const doc of userRequestsQuery.docs) {
                const data = doc.data();
                const requestDate = moment(data.requestDate);
                const timeAgo = requestDate.fromNow();
                // Fetch the user document to get the user's Fullname
                const userDoc = await db.collection('Users').doc(data.userId).get();
                const userFullname = userDoc.exists ? userDoc.data().Fullname : 'Unknown User';
                const request = {
                    id: doc.id,
                    timeAgo: timeAgo,
                    description: `Change ${data.dayToChange} to ${data.newDate}\nReason: ${data.reason}`,
                    userName: userFullname,
                    status: data.status,
                };
                userRequests.push(request);
            }
            return res.status(StatusCodes.OK).json(userRequests);
        }
    } catch (error) {
        console.error("Error retrieving requests:", error);
        res.status(StatusCodes.INTERNAL_SERVER_ERROR).send({
            message: 'Server error',
            error: error.message
        });
    }
};

module.exports = { getRequests};