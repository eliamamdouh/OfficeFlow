const { db } = require('../firebase-init');
const jwt = require('jsonwebtoken');
require('dotenv').config();

const getLocationForDate = (schedule, date) => {
    let location = null;

    console.log("Searching for date:", date);  // Log the date being searched for

    Object.keys(schedule).forEach(month => {
        const monthSchedule = schedule[month];
        Object.keys(monthSchedule).forEach(week => {
            monthSchedule[week].forEach(day => {
                console.log(`Checking date ${day.day} in week ${week} of month ${month}`);  // Log the current day being checked
                if (day.day === date) {
                    location = day.location;
                    console.log(`Match found: ${location} for date ${date}`);
                }
            });
        });
    });
    return location;
};


const submitRequest = async (req, res) => {
    try {
        console.log("1")
        const { authorization } = req.headers;
        const { newDate, dayToChange, reason } = req.body;
console.log(newDate)
console.log(dayToChange)
        if (!authorization) {
            return res.status(400).json( 'Authorization header is missing');
        }
        console.log("Headers received:", req.headers);

        if (!newDate || !dayToChange || !reason) {
            return res.status(400).json('New date, day to change, and reason are required' );
        }

        // Extract the JWT token
        const token = authorization.split(' ')[1];
        if (!token) {
            return res.status(401).json({ message: 'No token provided' });
        }

        // Verify and decode the token
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
        console.log("2")
        // Fetch the user data from the database
        const usersCollectionRef = db.collection('Users');
        const userDoc = await usersCollectionRef.doc(userId).get();
        if (!userDoc.exists) {
            return res.status(404).json({ message: 'User not found' });
        }

        const userData = userDoc.data();
        console.log("User data retrieved:", userData);

        if (newDate === dayToChange) {
            return res.status(400).json('The new date and the day to change cannot be the same' );
        }

        const schedule = userData.schedule;

        const locationForDayToChange = getLocationForDate(schedule, dayToChange);
        const locationForNewDate = getLocationForDate(schedule, newDate);
        console.log(locationForDayToChange )
        console.log(locationForNewDate  )
        if (locationForDayToChange === locationForNewDate) {
            console.log("3")
            return res.status(400).json( 'The selected days must be from different locations (one office, one home)');
        }

        // Ensure the managerName is present, otherwise handle the missing value
       // const managerName = userData.managerName || 'Unknown Manager';
                // Ensure the managerName is present, otherwise handle the missing value

                const projectId = userData.projectId
                console.log("4")
                // console.log("engyy:" + projID)
       
                //  const managerName = await db.collection('Users').where('projectId', '==', projID).where('role', '==', 'manager').get();
                //  console.log("engy manager name: "+managerName)
                const ManagerSnapshot = await db.collection('Users').where('projectId', '==', projectId).where('role', '==', 'Manager').get();

                let managerName = null;
                console.log("5")
                if (!ManagerSnapshot.empty) {
                    const managerDoc = ManagerSnapshot.docs[0]; // Get the first (and presumably only) document
                    managerName = managerDoc.data().Fullname; // Assuming 'name' is the field for the manager's name
                    console.log("engy manager name: " + managerName);

                    // Now you can save managerName to Firestore
                    // await db.collection('SomeCollection').add({
                    //     managerName: managerName,
                    //     // other fields...
                    // });
                } else {
                    console.log("No manager found for the given project.");
                }


        // Create a new request
        const requestsCollectionRef = db.collection('Requests');
        const requestDocRef = requestsCollectionRef.doc();

        // Ensure the data object is properly formatted and contains valid values
        const requestData = {
            userId,
            managerName,
            dayToChange,
            newDate,
            projectId,
            reason, // Save the reason in the database
            status: 'Pending',
            requestDate: new Date().toISOString(),
        };

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


const changeSchedule = async (req, res) => {
    try {
        const { username, newDate, dayToChange } = req.body;

        const userSnapshot = await db.collection('Users').where('username', '==', username).get();
        if (userSnapshot.empty) {
            return res.status(404).json({ message: 'User not found' });
        }

        const userDoc = userSnapshot.docs[0];
        const userData = userDoc.data();
        console.log(userData);
      
        if (newDate === dayToChange) {
            return res.status(400).json({ message: 'The new date and the day to change cannot be the same' });
        }

        const schedule = userData.schedule;

        // Helper variables to track locations
        let locationForDayToChange = null;
        let locationForNewDate = null;
        let dayToChangeFound = false;
        let newDateFound = false;

        // Traverse the schedule to find the days and their locations
        for (let week in schedule) {
            for (let entry of schedule[week]) {
                if (entry.day === dayToChange) {
                    locationForDayToChange = entry.location;
                    dayToChangeFound = true;
                }
                if (entry.day === newDate) {
                    locationForNewDate = entry.location;
                    newDateFound = true;
                }
                if (dayToChangeFound && newDateFound) break;
            }
            if (dayToChangeFound && newDateFound) break;
        }

        // Check if both dates were found
        if (!dayToChangeFound || !newDateFound) {
            return res.status(404).json({ message: 'One or both dates not found in the schedule' });
        }

        // Ensure the locations are different before swapping
        if (locationForDayToChange === locationForNewDate) {
            return res.status(400).json({ message: 'The selected days must be from different locations (one office, one home)' });
        }

        // Swap the locations
        for (let week in schedule) {
            for (let entry of schedule[week]) {
                if (entry.day === dayToChange) {
                    entry.location = locationForNewDate;
                }
                if (entry.day === newDate) {
                    entry.location = locationForDayToChange;
                }
            }
        }

        // Update the user's schedule in the database
        await db.collection('Users').doc(userDoc.id).update({
            schedule: schedule
        });

        return res.status(200).json({ message: 'Schedule updated successfully' });

    } catch (error) {
        console.error('Error updating schedule:', error);
        return res.status(500).json({ message: 'Internal server error' });
    }
};





module.exports = { submitRequest , changeSchedule};