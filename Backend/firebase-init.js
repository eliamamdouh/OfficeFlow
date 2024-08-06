// firebase-init.js
const admin = require("firebase-admin");
const path = require("path");
 
// Load service account key from firebase-init.json
const serviceAccountPath = path.join(__dirname, "firebase-init.json");
const serviceAccount = require(serviceAccountPath);
 
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://wfho-app-3af10.firebaseio.com"
});
 
const db = admin.firestore();
 
module.exports = { admin, db };