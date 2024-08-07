const { db } = require('../firebase-init');
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
require('dotenv').config();
const { StatusCodes } = require ("http-status-codes");

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
        const projectId = Math.floor(Math.random() * 10) + 1;
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

// Helper function to shuffle an array deterministically using a seed
const seededShuffleArray = (array, seed) => {
    let currentIndex = array.length, randomIndex;

    // While there remain elements to shuffle...
    while (currentIndex !== 0) {
        // Generate a pseudo-random number using the seed
        randomIndex = Math.floor(seedRandom(seed) * currentIndex);
        currentIndex--;

        // Swap it with the current element.
        [array[currentIndex], array[randomIndex]] = [array[randomIndex], array[currentIndex]];
    }

    return array;
};

// Pseudo-random number generator using a seed
const seedRandom = (seed) => {
    const x = Math.sin(seed++) * 10000;
    return x - Math.floor(x);
};

// Function to check if the office capacity for a specific day is below 300
const checkOfficeCapacity = async (date) => {
    const capacityDocRef = db.collection('OfficeCapacity').doc(date);
    const capacityDoc = await capacityDocRef.get();

    if (!capacityDoc.exists) {
        return true; // No users scheduled yet, capacity is available
    }

    const capacityData = capacityDoc.data();
    return capacityData.count < 300; // Return true if capacity is below 300
};

// Function to increment the office capacity count for a specific day
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
                Fullname: `User${i + 1}`,
                username: `user${i + 1}`,
                password: `password@123`,
                email: `user${i + 1}@example.com`,
                role: 'user',
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

// generateUsers(300); // Adjust the number as needed





const login = async (req, res) => {
    try {
        const { email, password } = req.body;

        if (!email || !password) {
            return res.status(400).json({ message: 'Email and password are required' });
        }

        let usersCollectionRef = db.collection('Users');
        let userQuerySnapshot = await usersCollectionRef.where('email', '==', email).get();


        if (userQuerySnapshot.empty) {
            return res.status(401).json({ message: 'Invalid email or password' });
        }
        
        const userDoc = userQuerySnapshot.docs[0];
        const userData = userDoc.data();
        
        const isPasswordValid = await bcrypt.compare(password, userData.password);

        if (!isPasswordValid) {
            return res.status(401).json({ message: 'Invalid password' });
        }

        const userId = userDoc.id;
        const role = userData.role;

        // Generate a JWT token
        const token = jwt.sign(
            { userId: userId, username: userData.username, email: userData.email, role: role },
            process.env.JWT_SECRET_KEY,
        );
        console.log(token)
        res.status(200).json({
            message: 'Login successful',
            userId: userId,
            role: role, 
            token: token,
        });
        console.log(role);
    } catch (error) {
        console.error("Error during login:", error);
        res.status(500).json({ message: 'Server error', error: error.message });
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




module.exports = { createUser ,login,getUserInfo };
