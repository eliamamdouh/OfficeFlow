// app.js
const express = require('express');
const { db } = require('./firebase-init'); // Import the Firebase init file to ensure the connection is established

const app = express();
const port = 3000;

app.use(express.json());

app.get('/', (req, res) => {
  res.send('Hello World!');
});

const userRoutes = require('./Routes/userRoutes.js');
app.use('/api/users', userRoutes);

const PORT = process.env.PORT || port;
app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});
