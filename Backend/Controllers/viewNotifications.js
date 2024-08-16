const { db } = require('../firebase-init');

const jwt = require('jsonwebtoken');

const viewNotifications = async (req, res) => {
    try {
        const { authorization } = req.headers;

        if (!authorization) {
            return res.status(400).json({ message: 'Authorization header is missing' });
        }

        const token = authorization.split(' ')[1];
        if (!token) {
            return res.status(401).json({ message: 'No token provided' });
        }

        const decoded = jwt.verify(token, process.env.JWT_SECRET_KEY);
        const userId = decoded.userId;

        if (!userId) {
            return res.status(400).json({ message: 'User ID is required' });
        }
        const notificationsQuerySnapshot = await db.collection('Notifications')
        .where('userId', '==', userId)
        .get();
    

        if (notificationsQuerySnapshot.empty) {
            return res.status(404).json({ message: 'No notifications found for this user' });
        }

        const notifications = notificationsQuerySnapshot.docs.map(doc => ({
            id: doc.id,
            ...doc.data()
        }));

        res.status(200).json(notifications);
    } catch (error) {
        console.error('Error retrieving notifications:', error);
        res.status(500).json({ message: 'Server error', error: error.message });
    }
};

module.exports = { viewNotifications };
