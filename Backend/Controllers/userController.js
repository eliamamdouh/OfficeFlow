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

    const weekDays = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday'];

    let schedule = {};

    for (let i = 0; i < 4; i++) {
        const weekNumber = i + 1;

        let officeDays, homeDays;
        if (weekNumber % 2 !== 0) {
            // Odd weeks: 3 days in the office, 2 days at home
            officeDays = shuffleArray([...weekDays]).slice(0, 3);
            homeDays = weekDays.filter(day => !officeDays.includes(day));
        } else {
            // Even weeks: 2 days in the office, 3 days at home
            homeDays = shuffleArray([...weekDays]).slice(0, 3);
            officeDays = weekDays.filter(day => !homeDays.includes(day));
        }

        schedule[`Week ${weekNumber}`] = weekDays.map(day => ({
            day,
            location: officeDays.includes(day) ? 'Office' : 'Home'
        }));
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

        // Generate a JWT token
        const token = jwt.sign(
            { userId: userDoc.id, username: userData.username, email: userData.email },
            process.env.JWT_SECRET_KEY, 
            
        );

        res.status(200).json({ message: 'Login successful', token });
    } catch (error) {
        console.error("Error during login:", error);
        res.status(500).json({ message: 'Server error', error: error.message });
    }
};




module.exports = { createUser ,loginUser};
