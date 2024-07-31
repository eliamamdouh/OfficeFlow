// userController.js
const { db } = require('../firebase-init');
const { collection, doc, setDoc } = require('firebase/firestore');

const createUser = async (req, res) => {
    try {
        const { username, password, email, userId, managerId } = req.body;

        if (!username || !password || !email) {
            return res.status(400).json({ message: 'Username, password, and email are required' });
        }

        // Check if the user already exists in Firestore based on the email
        const usersCollectionRef = db.collection('users');
        const existingUserQuery = await usersCollectionRef.where('email', '==', email).get();

        if (!existingUserQuery.empty) {
            return res.status(400).json({ message: 'User already exists' });
        }

        // Create a new document in the 'users' collection
        const userDocRef = usersCollectionRef.doc(); // Auto-generate a document ID

        await userDocRef.set({
            username,
            email,
            userId,
            managerId,
            password,  // Consider hashing the password before storing
            createdAt: new Date().toISOString(),
            updatedAt: new Date().toISOString()
        });

        res.status(201).json({ message: 'User created successfully', userId: userDocRef.id });
    } catch (error) {
        console.error("Error creating user:", error);
        res.status(500).json({ message: 'Server error', error: error.message });
    }
};

module.exports = { createUser };
