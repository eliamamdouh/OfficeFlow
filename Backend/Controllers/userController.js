// userController.js

const { db } = require('../firebase-init');
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
require('dotenv').config();


const createUser = async (req, res) => {
    try {
        const {Fullname, username, password, email, managerName } = req.body;

        if (!username || !password || !email) {
            return res.status(400).json({ message: 'Username, password, and email are required' });
        }

        const usersCollectionRef = db.collection('users');
        const existingUserQuery = await usersCollectionRef.where('email', '==', email).get();

        if (!existingUserQuery.empty) {
            return res.status(400).json({ message: 'User already exists' });
        }
        const hashedPassword = await bcrypt.hash(password, 10);

        const userDocRef = usersCollectionRef.doc(); // Auto-generate a document ID

        const createdAt = new Date().toISOString();
        const updatedAt = createdAt;

        // Generate a schedule for the user
        const schedule = generateAlternatingUserSchedule();

        await userDocRef.set({
            Fullname,
            username,
            email,
            managerName,
            password: hashedPassword, 
            createdAt,
            updatedAt,
            schedule  
        });

        res.status(201).json({ message: 'User created successfully', userId: userDocRef.id });
    } catch (error) {
        console.error("Error creating user:", error);
        res.status(500).json({ message: 'Server error', error: error.message });
    }
};

const generateAlternatingUserSchedule = () => {
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
                officeDays = shuffleArray([2, 3, 4, 5, 6]).slice(0, 3);
                homeDays = [2, 3, 4, 5, 6].filter(day => !officeDays.includes(day));
            } else {
                // Even weeks: 2 days in the office, 3 days at home
                homeDays = shuffleArray([2, 3, 4, 5, 6]).slice(0, 3);
                officeDays = [2, 3, 4, 5, 6].filter(day => !homeDays.includes(day));
            }
        }

        const formattedDate = date.toISOString().split('T')[0]; // Format date as YYYY-MM-DD

        if (!schedule[`Week ${weekNumber}`]) {
            schedule[`Week ${weekNumber}`] = [];
        }

        schedule[`Week ${weekNumber}`].push({
            day: formattedDate,
            location: officeDays.includes(weekday) ? 'Office' : 'Home'
        });
    }

    return schedule;
};

const shuffleArray = (array) => {
    for (let i = array.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [array[i], array[j]] = [array[j], array[i]];
    }
    return array;
};







const loginUser = async (req, res) => {
    try {
        const { email, password } = req.body;

        if (!email || !password) {
            return res.status(400).json({ message: 'Email and password are required' });
        }

        const usersCollectionRef = db.collection('users');
        const userQuerySnapshot = await usersCollectionRef.where('email', '==', email).get();

        if (userQuerySnapshot.empty) {
            return res.status(401).json({ message: 'Invalid email or password' });
        }

        const userDoc = userQuerySnapshot.docs[0];
        const userData = userDoc.data();

        const isPasswordValid = await bcrypt.compare(password, userData.password);

        if (!isPasswordValid) {
            return res.status(401).json({ message: 'Invalid email or password' });
        }
        const userId = userDoc.id;

        // Generate a JWT token
        const token = jwt.sign(
            { userId: userDoc.id, username: userData.username, email: userData.email },
            process.env.JWT_SECRET_KEY, 
            
        );

        res.status(200).json({
            message: 'Login successful',
            userId: userId, 
        });
        console.log(token)
    } catch (error) {
        console.error("Error during login:", error);
        res.status(500).json({ message: 'Server error', error: error.message });
    }
};

const getUserInfo = async (req, res) => {
    try {
        const userId = req.params.userId;

        const userDoc = await db.collection('users').doc(userId).get();

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




module.exports = { createUser ,loginUser,getUserInfo };
