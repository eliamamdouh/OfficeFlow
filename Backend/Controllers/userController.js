const { db } = require('../firebase-init');
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
require('dotenv').config();
const { StatusCodes } = require ("http-status-codes");
const { use } = require('../Routes/userRoutes');

const createUser = async (req, res) => {
    try {
        const { Fullname, username, password, email, role } = req.body;

        if (!username || !password || !email) {
            return res.status(StatusCodes.BAD_REQUEST).json({ message: 'Username, password, and email are required' });
        }

        const usersCollectionRef = db.collection('Users');
        const existingUserQuery = await usersCollectionRef.where('email', '==', email).get();

        if (!existingUserQuery.empty) {
            return res.status(StatusCodes.BAD_REQUEST).json({ message: 'User already exists' });
        }

        const hashedPassword = await bcrypt.hash(password, 10);

        const userDocRef = usersCollectionRef.doc(); // Auto-generate a document ID

        const createdAt = new Date().toISOString();
        const updatedAt = createdAt;
        const projectId = 10;
        const schedule = await generateScheduleForProject(projectId);

        const userData = {
            Fullname,
            username,
            email,
            role,
            password: hashedPassword,
            createdAt,
            updatedAt,
            schedule,
            projectId
        };


        await userDocRef.set(userData);

        res.status(StatusCodes.CREATED).json({ message: 'User created successfully', userId: userDocRef.id });
    } catch (error) {
        console.error("Error creating user:", error);
        res.status(StatusCodes.INTERNAL_SERVER_ERROR).json({ message: 'Server error', error: error.message });
    }
};

const generateScheduleForProject = async (projectId) => {
    const seed = projectId; // Use projectId as a seed for deterministic scheduling
    return await generateAlternatingUserSchedule(seed);
};

const generateAlternatingUserSchedule = async (seed) => {
    const currentDate = new Date();
    const year = currentDate.getFullYear();
    const month = currentDate.getMonth(); // 0-based, so 7 = August
    const daysInMonth = new Date(year, month + 1, 0).getDate(); // Get the number of days in the current month

    let schedule = {};
    let weekNumber = 0;
    let officeDays = [];
    let homeDays = [];

    for (let day = 1; day <= daysInMonth; day++) {
        const date = new Date(year, month, day);
        const weekday = date.getDay(); // 0 = Sunday, 1 = Monday, ..., 6 = Saturday

        // Skip weekends (Saturday and Sunday)
        if (weekday === 0 || weekday === 1) { // Skip Sunday and Monday
            continue;
        }

        // Start a new week on Tuesday
        if (weekday === 2) {
            weekNumber++; // Increment week number at the start of a new week

            if (weekNumber % 2 !== 0) {
                // Odd weeks: 3 days in the office, 2 days at home
                officeDays = seededShuffleArray([2, 3, 4, 5, 6], seed).slice(0, 3);
                homeDays = [2, 3, 4, 5, 6].filter(day => !officeDays.includes(day));
            } else {
                // Even weeks: 2 days in the office, 3 days at home
                homeDays = seededShuffleArray([2, 3, 4, 5, 6], seed).slice(0, 3);
                officeDays = [2, 3, 4, 5, 6].filter(day => !homeDays.includes(day));
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
            location: location
        });
    }

    return schedule;
};

const seededShuffleArray = (array, seed) => {
    let currentIndex = array.length, randomIndex;

    while (currentIndex !== 0) {

        randomIndex = Math.floor(seedRandom(seed) * currentIndex);
        currentIndex--;

        [array[currentIndex], array[randomIndex]] = [array[randomIndex], array[currentIndex]];
    }

    return array;
};

const seedRandom = (seed) => {
    const x = Math.sin(seed++) * 10000;
    return x - Math.floor(x);
};


const checkOfficeCapacity = async (date) => {
    const capacityDocRef = db.collection('OfficeCapacity').doc(date);
    const capacityDoc = await capacityDocRef.get();

    if (!capacityDoc.exists) {
        return true;
    }

    const capacityData = capacityDoc.data();
    return capacityData.count < 300;
};

const incrementOfficeCapacity = async (date) => {
    const capacityDocRef = db.collection('OfficeCapacity').doc(date);
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
const generateUsers = async (numUsers) => {
    for (let i = 0; i < numUsers; i++) {
        const req = {
            body: {
                Fullname: `Manager10`,
                username: `Manager10`,
                password: `password@123`,
                email: `Manager10@Deloitte.com`,
                role: 'Manager',
            }
        };
        const res = {
            status: (statusCode) => ({
                json: (data) => console.log(`Status: ${statusCode}`, data)
            })
        };
        await createUser(req, res);
    }
};

// generateUsers(1); // Adjust the number as needed

const login = async (req, res) => {
    try {
        const { email, password } = req.body;

        if (!email || !password) {
            return res.status(StatusCodes.BAD_REQUEST).json({ errorMessage: 'Email and password are required' });
        }

        let usersCollectionRef = db.collection('Users');
        let userQuerySnapshot = await usersCollectionRef.where('email', '==', email).get();

        if (userQuerySnapshot.empty) {
            return res.status(StatusCodes.UNAUTHORIZED).json({ errorMessage: 'Login failed: User not found' });
        }

        const userDoc = userQuerySnapshot.docs[0];
        const userData = userDoc.data();
        
        const isPasswordValid = await bcrypt.compare(password, userData.password);

        if (!isPasswordValid) {
            return res.status(StatusCodes.UNAUTHORIZED).json({ errorMessage: 'Login failed: Password does not match' });
        }

        const userId = userDoc.id;
        const role = userData.role;
        console.log("Engy betdawar 3ala dah: " + role)

        const token = jwt.sign(
            { userId: userId, username: userData.username, email: userData.email, role: role },
            process.env.JWT_SECRET_KEY,
            { expiresIn: '1h' }
        );

        res.status(StatusCodes.OK).json({
            message: 'Login successful',
            userId: userId,
            role: role, 
            token: token,
        });
    } catch (error) {
        console.error("Error during login:", error);
        res.status(StatusCodes.INTERNAL_SERVER_ERROR).json({ errorMessage: 'Server error', details: error.message });
    }
};


const getUserInfo = async (req, res) => {
    try {
        const userId = req.params.userId;

        const userDoc = await db.collection('Users').doc(userId).get();

        if (!userDoc.exists) {
            return res.status(404).json({ message: 'User not found' });
        }

        const userData = userDoc.data();
        const currentDate = new Date().toISOString().split('T')[0];

        let todaySchedule = null;
        Object.keys(userData.schedule).forEach(week => {
            const weekSchedule = userData.schedule[week];
            weekSchedule.forEach(day => {
                if (day.day === currentDate) {
                    todaySchedule = day.location;
                }
            });
        });

        res.status(200).json({
            name: userData.username,
            todaySchedule: todaySchedule
        });

    } catch (error) {
        console.error('Error getting user info:', error);
        res.status(500).json({ message: 'Server error', error: error.message });
    }
};

const countUsers = async () => {
    try {
        const usersCollectionRef = db.collection('Users');
        const snapshot = await usersCollectionRef.get();
        const userCount = snapshot.size; // The size property gives the number of documents in the collection
        console.log(`Number of users: ${userCount}`);
        return userCount;
    } catch (error) {
        console.error("Error counting users:", error);
        throw error;
    }
};

// Example usage
// countUsers();

const countUsersByRole = async () => {
    try {
        const usersCollectionRef = db.collection('Users');

        // Count users with role 'Employee'
        const employeeQuerySnapshot = await usersCollectionRef.where('role', '==', 'Employee').get();
        const employeeCount = employeeQuerySnapshot.size;

        // Count users with role 'Manager'
        const managerQuerySnapshot = await usersCollectionRef.where('role', '==', 'Manager').get();
        const managerCount = managerQuerySnapshot.size;

        console.log(`Number of Employees: ${employeeCount}`);
        console.log(`Number of Managers: ${managerCount}`);

        return { employeeCount, managerCount };
    } catch (error) {
        console.error("Error counting users by role:", error);
        throw error;
    }
};

// Example usage
// countUsersByRole();

const getTeamMembers = async (req, res) => {
    try {
        const { managerId } = req.body;
        console.log(managerId);
        
        // Retrieve the manager's document based on managerId
        const userDoc = await db.collection('Users').doc(managerId).get();

        if (!userDoc.exists) {
            return res.status(404).json({ message: 'User not found' });
        }

        const userData = userDoc.data();
        const projectId = userData.projectId;
        console.log(projectId);

        // Get all users with the same projectId
        const usersSnapshot = await db.collection('Users').where('projectId', '==', projectId).get();

        if (usersSnapshot.empty) {
            return res.status(404).json({ message: 'No team members found for this project' });
        }

        // Prepare a list to store team members and their schedules
        let teamMembers = [];

        // Iterate over each user and fetch their data including schedules
        usersSnapshot.forEach(userDoc => {
            const userData = userDoc.data();

            teamMembers.push({
                userId: userDoc.id,
                name: userData.name,
                role: userData.role,
                schedules: userData.schedule , // Assuming schedules are stored in an array within userData
            });
        });

        return res.status(200).json({ teamMembers });

    } catch (error) {
        console.error('Error fetching team members:', error);
        return res.status(500).json({ message: 'Internal server error' });
    }
};

 

module.exports = { createUser ,login,getUserInfo, getTeamMembers};
