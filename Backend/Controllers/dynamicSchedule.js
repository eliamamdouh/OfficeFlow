const { db } = require('../firebase-init.js');
const jwt = require('jsonwebtoken');
require('dotenv').config();
const { StatusCodes } = require('http-status-codes');



const generateDynamicSchedule = async (req, res) => {
    try {
        const { authorization } = req.headers;
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
                return res.status(StatusCodes.UNAUTHORIZED).json({
                    message: 'Token expired'
                });
            }
            throw error;
        }

        const managerId = decoded.userId;
        const managerDoc = await db.collection('Users').doc(managerId).get();

        if (!managerDoc.exists) {
            return res.status(StatusCodes.NOT_FOUND).send('Manager not found');
        }

        const managerData = managerDoc.data();

        if (managerData.username !== 'SuperManager' && managerData.email !== 'SuperManager@Deloitte.com') {
            return res.status(StatusCodes.FORBIDDEN).json({
                message: 'Only the Super Manager can generate dynamic schedules'
            });
        }

        const { oddWeekOfficeDays, evenWeekOfficeDays } = req.body;
        if (!oddWeekOfficeDays || !evenWeekOfficeDays) {
            return res.status(StatusCodes.BAD_REQUEST).send('Missing number of office days for odd or even weeks');
        }

        // Determine if the schedule should be for the next month
        const currentDate = new Date();
        let year = currentDate.getFullYear();
        let month = currentDate.getMonth(); // 0-based, so 7 = August
        
//comment if this month
        if (currentDate.getDate() !== 1) {
            // If it's not the first day of the month, generate the schedule for the next month
            month += 1;
            if (month > 11) {
                month = 0; // Wrap around to January
                year += 1;
            }
        }

        // Generate the schedule for the specified month and year
        const schedule = await generateAlternatingUserSchedule({
            year,
            month,
            oddWeekOfficeDays,
            evenWeekOfficeDays,
            projectId: decoded.projectId,
        });

        // Fetch all users from the database
        const usersSnapshot = await db.collection('Users').get();
        if (usersSnapshot.empty) {
            return res.status(StatusCodes.NOT_FOUND).send('No users found in the database');
        }

        // Map the generated schedule to all users, appending it to the existing schedule
        const batch = db.batch();
        usersSnapshot.forEach((userDoc) => {
            const userRef = db.collection('Users').doc(userDoc.id);
            const userSchedule = userDoc.data().schedule || {}; // Fetch the existing schedule

            // Create a new month entry in the schedule if it doesn't exist
            const monthKey = `Month ${month + 1}-${year}`; // Example: Month 8-2024 for August 2024

            if (!userSchedule[monthKey]) {
                userSchedule[monthKey] = {};
            }

            // Append the new schedule to the user's existing schedule
            for (const [week, days] of Object.entries(schedule)) {
                if (!userSchedule[monthKey][week]) {
                    userSchedule[monthKey][week] = [];
                }
                userSchedule[monthKey][week] = [...userSchedule[monthKey][week], ...days];
            }

            batch.update(userRef, { schedule: userSchedule });
        });

        await batch.commit();

        return res.status(StatusCodes.OK).json({
            message: 'Schedule successfully generated and assigned to all employees',
            schedule: schedule,
        });
    } catch (error) {
        console.error('Error generating dynamic schedule:', error);
        return res.status(StatusCodes.INTERNAL_SERVER_ERROR).send({
            message: 'Server error',
            error: error.message,
        });
    }
};

const generateAlternatingUserSchedule = async ({ year, month, oddWeekOfficeDays, evenWeekOfficeDays, projectId }) => {
  const daysInMonth = new Date(year, month + 1, 0).getDate(); // Get the number of days in the specified month

  let schedule = {};
  let weekNumber = 0;
  let officeDays = [];
  let homeDays = [];

  for (let day = 1; day <= daysInMonth; day++) {
    const date = new Date(year, month, day);
    const weekday = date.getDay(); // 0 = Sunday, 1 = Monday, ..., 6 = Saturday

    // Skip weekends (Saturday and Sunday)
    if (weekday === 0 || weekday === 1) {
      continue;
    }

    // Start a new week on Tuesday
    if (weekday === 2) {
      weekNumber++; // Increment week number at the start of a new week

      if (weekNumber % 2 !== 0) {
        // Odd weeks: use the manager-defined number of days in the office
        officeDays = seededShuffleArray([2, 3, 4, 5, 6], projectId).slice(0, oddWeekOfficeDays);
        homeDays = [2, 3, 4, 5, 6].filter((day) => !officeDays.includes(day));
      } else {
        // Even weeks: use the manager-defined number of days in the office
        officeDays = seededShuffleArray([2, 3, 4, 5, 6], projectId).slice(0, evenWeekOfficeDays);
        homeDays = [2, 3, 4, 5, 6].filter((day) => !officeDays.includes(day));
      }
    }

    const formattedDate = date.toISOString().split('T')[0]; // Format date as YYYY-MM-DD

    if (!schedule[`Week ${weekNumber}`]) {
      schedule[`Week ${weekNumber}`] = [];
    }

    const isOfficeCapacityAvailable = await checkOfficeCapacity(formattedDate);
    const location = isOfficeCapacityAvailable && officeDays.includes(weekday) ? 'Office' : 'Home';

    if (location === 'Office') {
      await incrementOfficeCapacity(formattedDate);
    }

    schedule[`Week ${weekNumber}`].push({
      day: formattedDate,
      location: location,
    });
  }

  return schedule;
};


const seededShuffleArray = (array, seed) => {
    let currentIndex = array.length,
      randomIndex;
  
    while (currentIndex !== 0) {
      randomIndex = Math.floor(seedRandom(seed) * currentIndex);
      currentIndex--;
  
      [array[currentIndex], array[randomIndex]] = [
        array[randomIndex],
        array[currentIndex],
      ];
    }
  
    return array;
  };
  
  const seedRandom = (seed) => {
    const x = Math.sin(seed++) * 10000;
    return x - Math.floor(x);
  };
  
  const checkOfficeCapacity = async (date) => {
    const capacityDocRef = db.collection("OfficeCapacity").doc(date);
    const capacityDoc = await capacityDocRef.get();
  
    if (!capacityDoc.exists) {
      return true;
    }
  
    const capacityData = capacityDoc.data();
    return capacityData.count < 300;
  };
  
  const incrementOfficeCapacity = async (date) => {
    const capacityDocRef = db.collection("OfficeCapacity").doc(date);
    await db.runTransaction(async (transaction) => {
      const capacityDoc = await transaction.get(capacityDocRef);
  
      if (!capacityDoc.exists) {
        transaction.set(capacityDocRef, { count: 1 });
      } else {
        const newCount = capacityDoc.data().count + 1;
        transaction.update(capacityDocRef, { count: newCount });
      }
    });
  };


const clearAllUserSchedules = async (req, res) => {
    try {
        // Fetch all users from the database
        const usersSnapshot = await db.collection('Users').get();
        if (usersSnapshot.empty) {
            return res.status(StatusCodes.NOT_FOUND).send('No users found in the database');
        }

        // Create a batch to clear schedules for all users
        const batch = db.batch();
        usersSnapshot.forEach((userDoc) => {
            const userRef = db.collection('Users').doc(userDoc.id);

            // Clear the schedule field for each user
            batch.update(userRef, { schedule: {} });
        });

        // Commit the batch operation to update the database
        await batch.commit();

        return res.status(StatusCodes.OK).json({
            message: 'All user schedules have been successfully cleared',
        });
    } catch (error) {
        console.error('Error clearing user schedules:', error.message);
        return res.status(StatusCodes.INTERNAL_SERVER_ERROR).json({
            message: 'Server error',
            error: error.message,
        });
    }
};

module.exports = {
    clearAllUserSchedules,
};

  
module.exports = { generateDynamicSchedule, clearAllUserSchedules };





