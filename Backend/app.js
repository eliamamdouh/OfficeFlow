// app.js
const express = require('express');
const { db } = require('./firebase-init'); // Import the Firebase init file to ensure the connection is established

const app = express();
const DEFAULT_PORT = 3000;
const DEFAULT_HOST = '0.0.0.0';
const userRoutes = require('./Routes/userRoutes.js');
const { getTeamMembers } = require('./Controllers/userController');
const {changeSchedule} = require('./Controllers/submitRequestContoller.js')


app.use(express.json());

app.get('/', (req, res) => {
  res.send('Hello World!');
});

app.get('/getTeamMembers',getTeamMembers)
app.get('/changeSchedule',changeSchedule)

app.use('/api/users', userRoutes);


const PORT = process.env.PORT || DEFAULT_PORT;
const HOST = process.env.HOST || DEFAULT_HOST;

app.listen(PORT, HOST, () => {
  console.log(`Server is running on http://${HOST}:${PORT}`);
});