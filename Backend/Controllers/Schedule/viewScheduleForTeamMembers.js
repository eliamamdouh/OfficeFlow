const { db } = require("../../firebase-init");
const { StatusCodes } = require("http-status-codes");
const jwt = require("jsonwebtoken");
require("dotenv").config();

const viewTeamMembersSchedule = async (req, res) => {
  try {
    const { authorization } = req.headers;
    const { userId } = req.body;

    if (!authorization) {
      return res
        .status(StatusCodes.BAD_REQUEST)
        .send("Authorization token is required");
    }
    console.log("Headers received:", req.headers);

    const token = authorization.split(" ")[1];

    let decodedToken;
    try {
      decodedToken = jwt.verify(token, process.env.JWT_SECRET_KEY);
    } catch (error) {
      return res
        .status(StatusCodes.UNAUTHORIZED)
        .json({ message: "Unauthorized" });
    }

    const managerId = decodedToken.userId;

    // Retrieve the manager's document to verify the manager's existence
    const managerDoc = await db.collection("Users").doc(managerId).get();

    if (!managerDoc.exists) {
      return res.status(StatusCodes.NOT_FOUND).send("Manager not found");
    }

    const managerData = managerDoc.data();
    const projectId = managerData.projectId;

    // Check if the manager is "SuperManager"
    let usersSnapshot;
    if (managerData.username === "SuperManager") {
      // change later to role
      usersSnapshot = await db.collection("Users").get();
    } else {
      // Get all users with the same projectId
      usersSnapshot = await db
        .collection("Users")
        .where("projectId", "==", projectId)
        .get();
    }

    const userDoc = await db.collection("Users").doc(userId).get();

    if (!userDoc.exists) {
      return res.status(StatusCodes.NOT_FOUND).send("User not found");
    }

    const userData = userDoc.data();
    const schedule = userData.schedule;

    res.status(StatusCodes.OK).json({ schedule: schedule });
  } catch (error) {
    console.error("Error viewing schedule:", error);
    res
      .status(StatusCodes.INTERNAL_SERVER_ERROR)
      .send("Error viewing schedule");
  }
};

module.exports = { viewTeamMembersSchedule };
