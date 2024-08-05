const { db } = require('../firebase-init');
const bcrypt = require('bcrypt');

const jwt = require('jsonwebtoken');
require('dotenv').config();


const createManager = async (req, res) => {
    try {
        const { username, password, email } = req.body;

        if (!username || !password || !email) {
            return res.status(400).json({ message: 'Username, password, and email are required' });
        }

        const usersCollectionRef = db.collection('Managers');
        const existingUserQuery = await usersCollectionRef.where('email', '==', email).get();

        if (!existingUserQuery.empty) {
            return res.status(400).json({ message: 'User already exists' });
        }
        const hashedPassword = await bcrypt.hash(password, 10);

        const userDocRef = usersCollectionRef.doc(); // Auto-generate a document ID

        const createdAt = new Date().toISOString();
        const updatedAt = createdAt;


        await userDocRef.set({
            username,
            email,
            password: hashedPassword, 
            createdAt,
            updatedAt,
        });

        res.status(201).json({ message: 'User created successfully', userId: userDocRef.id });
    } catch (error) {
        console.error("Error creating user:", error);
        res.status(500).json({ message: 'Server error', error: error.message });
    }
};
const loginManager = async (req, res) => {
    try {
        const { email, password } = req.body;

        if (!email || !password) {
            return res.status(400).json({ message: 'Email and password are required' });
        }

        const usersCollectionRef = db.collection('managers');
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
            token : token,

        });
        console.log(token)
    } catch (error) {
        console.error("Error during login:", error);
        res.status(500).json({ message: 'Server error', error: error.message });
    }
};

const checkEmail = async (req, res) => {
    const email = req.body.email; 

    try {
       
        const userSnapshot = await db.collection('users').where('email', '==', email).get();
        if (!userSnapshot.empty) {
            return res.status(200).send('user'); 
        }

        const managerSnapshot = await db.collection('managers').where('email', '==', email).get();
        if (!managerSnapshot.empty) {
            return res.status(200).send('manager'); 
        }

        // If no user or manager is found
        return res.status(404).send('not found'); 

    } catch (error) {
        console.error('Error checking email:', error);
        return res.status(500).send('error'); 
    }
};



module.exports = { createManager,loginManager,checkEmail };