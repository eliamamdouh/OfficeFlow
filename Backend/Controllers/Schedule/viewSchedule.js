const { db } = require("../../firebase-init");
const { StatusCodes } = require("http-status-codes");
const jwt = require("jsonwebtoken");
require("dotenv").config();

const viewSchedule = async (req, res) => {
  try {
    const { authorization } = req.headers;

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
      console.log(decodedToken);
    } catch (error) {
      if (error.name === "TokenExpiredError") {
        return res.status(StatusCodes.UNAUTHORIZED).send("Unauthorized");
      }
      //   handle unreal token
      throw error; // Re-throw other errors
    }

    const userId = decodedToken.userId;

    const usersCollectionRef = db.collection("Users");
    const userDoc = await usersCollectionRef.doc(userId).get();

    if (!userDoc.exists) {
      return res.status(StatusCodes.NOT_FOUND).send("User not found");
    }

    const userData = userDoc.data();
    const schedule = userData.schedule;

    res
      .status(StatusCodes.OK)
      .json({ message: "Schedule retrieved successfully", schedule });
  } catch (error) {
    console.error("Error viewing schedule:", error);
    res.status(StatusCodes.INTERNAL_SERVER_ERROR).send("Server error");
  }
};

module.exports = { viewSchedule };
