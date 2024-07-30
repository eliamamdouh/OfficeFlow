const express = require('express');
const mongoose = require('mongoose');
const userRoutes = require('./Routes/userRoutes');

const app = express();
const port = 3000;

// Middleware to parse JSON bodies
app.use(express.json());

// Use user routes
app.use('/api', userRoutes);

// Connect to MongoDB
mongoose.connect('mongodb+srv://sama:sama@cluster0.fp0hphc.mongodb.net/?retryWrites=true&w=majority&ssl=true'
).then(() => {
    console.log('Connected to MongoDB');
}).catch(err => {
    console.error('Error connecting to MongoDB', err);
});

// Start the server
app.listen(port, () => {
    console.log(`Server is running on http://localhost:${port}`);
});
