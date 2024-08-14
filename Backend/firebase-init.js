const admin = require("firebase-admin");
const path = require("path");

// Load service account key from firebase-init.json
const serviceAccountPath = path.join(__dirname, "firebase-init.json");
const serviceAccount = require(serviceAccountPath);

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://wfho-app-3af10.firebaseio.com"
});

const sendNotification = async (registrationToken, messageTitle, messageBody) => {
  console.log("here");
  const message = {
    notification: {
      title: messageTitle,
      body: messageBody,
    },
    token: registrationToken,
  };

  try {
    const response = await admin.messaging().send(message);
    console.log('Successfully sent message:', response);
  } catch (error) {
    console.error('Error sending message:', error.message, error.stack);
    throw error; // Rethrow the error so it can be caught by the route
  }
};

const db = admin.firestore();

module.exports = { sendNotification, admin, db };
