const { db } = require('../firebase-init');

const viewSchedule = async (req, res) => {
    try {
        const { username } = req.body; // or req.params ???

        if (!username) {
            return res.status(400).json({ message: 'Username is required' });
        }

        // Query Firestore to find the user by username
        const usersCollectionRef = db.collection('users');
        const userQuery = await usersCollectionRef.where('username', '==', username).get();

        if (userQuery.empty) {
            return res.status(404).json({ message: 'User not found' });
        }

        const userDoc = userQuery.docs[0];
        const userData = userDoc.data();

        // Extract the schedule from the user data
        const schedule = userData.schedule;

        // Return the schedule
        res.status(200).json({ message: 'Schedule retrieved successfully', schedule });
    } catch (error) {
        console.error("Error viewing schedule:", error);
        res.status(500).json({ message: 'Server error', error: error.message });
    }
};

module.exports = { viewSchedule };
