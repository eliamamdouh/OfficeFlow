// app.js
const express = require('express');
const { db } = require('./firebase-init'); // Import the Firebase init file to ensure the connection is established

const app = express();
const DEFAULT_PORT = 3000;
const DEFAULT_HOST = '0.0.0.0';
const userRoutes = require('./Routes/userRoutes.js');

//const {viewRequests}= require('./Controllers/viewRequestsController.js');

const { getTeamMembers } = require('./Controllers/userController');
const {changeSchedule} = require('./Controllers/submitRequestContoller.js')



const {sendNotification} = require('./firebase-init');
const { viewNotifications } = require('./Controllers/viewNotifications.js');


app.use(express.json());


app.get('/', (req, res) => {
  res.send('Hello World!');
});

app.get('/getTeamMembers',getTeamMembers)
app.get('/changeSchedule',changeSchedule)
app.get('/api/viewNotifications',viewNotifications)
app.use('/api/users', userRoutes);
//app.get('/api/view-requests', viewRequests);



app.post('/sendnotification', async (req, res) => {
  console.log("hereeeeeeeeeeee")
  const { token } = req.body; // Only use the token for this test

  try {
    await sendNotification(token, "Test Title", "Test Body");
    res.status(200).send('Notification sent successfully!');
  } catch (error) {
    console.error('Failed to send notification:', error);
    res.status(500).send(`Failed to send notification: ${error.message}`);
  }
});



const PORT = process.env.PORT || DEFAULT_PORT;
const HOST = process.env.HOST || DEFAULT_HOST;

app.listen(PORT, HOST, () => {
  console.log(`Server is running on http://${HOST}:${PORT}`);
});
