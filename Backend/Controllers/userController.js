const { db } = require('../firebase-init');
const { collection, doc, setDoc } = require('firebase/firestore');
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
require('dotenv').config();
const { StatusCodes } = require ("http-status-codes");

const createUser = async (req, res) => {
    try {
        const {Fullname, username, password, email, managerName } = req.body;

        if (!username || !password || !email) {
            return res.status(StatusCodes.BAD_REQUEST).json({ message: 'Username, password, and email are required' });
        }

        const usersCollectionRef = db.collection('users');
        const existingUserQuery = await usersCollectionRef.where('email', '==', email).get();

        if (!existingUserQuery.empty) {
            return res.status(StatusCodes.BAD_REQUEST).json({ message: 'User already exists' });
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

        res.status(StatusCodes.CREATED).json({ message: 'User created successfully', userId: userDocRef.id });
    } catch (error) {
        console.error("Error creating user:", error);
        res.status(StatusCodes.INTERNAL_SERVER_ERROR).json({ message: 'Server error', error: error.message });
    }
};

const generateAlternatingUserSchedule = () => {
    
    const weekDays = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday'];
    const currentDate = new Date();
    
    const dayOfWeek = currentDate.getDay();
    const daysUntilMonday = (dayOfWeek === 0 ? 1 : 8) - dayOfWeek; 
    let nextMonday = new Date(currentDate);
    nextMonday.setDate(currentDate.getDate() + daysUntilMonday);

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

        schedule[`Week ${weekNumber}`] = weekDays.map((day, index) => {
            let currentDayDate = new Date(nextMonday);
            currentDayDate.setDate(nextMonday.getDate() + index + (weekNumber - 1) * 7);
            return {
                day,
                date: currentDayDate.toISOString().split('T')[0], // YYYY-MM-DD
                location: officeDays.includes(day) ? 'Office' : 'Home'
            };
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
            return res.status(StatusCodes.BAD_REQUEST).json({ message: 'Email and password are required' });
        }

        const usersCollectionRef = db.collection('users');
        const userQuerySnapshot = await usersCollectionRef.where('email', '==', email).get();

        if (userQuerySnapshot.empty) {
            return res.status(StatusCodes.UNAUTHORIZED).json({ message: 'Invalid email or password' });
        }

        const userDoc = userQuerySnapshot.docs[0];
        const userData = userDoc.data();

        const isPasswordValid = await bcrypt.compare(password, userData.password);

        if (!isPasswordValid) {
            return res.status(StatusCodes.UNAUTHORIZED).json({ message: 'Invalid email or password' });
        }

        // Generate a JWT token
        const token = jwt.sign(
            { userId: userDoc.id, username: userData.username, email: userData.email },
            process.env.JWT_SECRET_KEY, 
            
        );

        res.status(StatusCodes.OK).json({ message: 'Login successful', token });
    } catch (error) {
        console.error("Error during login:", error);
        res.status(StatusCodes.INTERNAL_SERVER_ERROR).json({ message: 'Server error', error: error.message });
    }
};




module.exports = { createUser ,loginUser};
