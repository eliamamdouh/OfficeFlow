const { db } = require("../firebase-init.js");
const jwt = require("jsonwebtoken");
require("dotenv").config();
const { StatusCodes } = require("http-status-codes");

const countAllRequests = async (req, res) => {
  try {
    const { authorization } = req.headers;

    if (!authorization) {
      return res
        .status(StatusCodes.BAD_REQUEST)
        .send("Authorization header is missing");
    }

    const token = authorization.split(" ")[1];

    if (!token) {
      return res.status(StatusCodes.UNAUTHORIZED).send("No token provided");
    }

    let decoded;

    try {
      decoded = jwt.verify(token, process.env.JWT_SECRET_KEY);
    } catch (error) {
      if (error.name === "TokenExpiredError") {
        return res.status(StatusCodes.UNAUTHORIZED).json({
          message: "Token expired",
        });
      }

      throw error;
    }

    const managerId = decoded.userId;

    // Get manager's data
    const managerDoc = await db.collection("Users").doc(managerId).get();

    if (!managerDoc.exists) {
      return res.status(StatusCodes.NOT_FOUND).send("Manager not found");
    }

    const managerData = managerDoc.data();

    if (managerData.role === "SuperManager") {
      // Count the number of requests with each status
      const totalRequestsSnapshot = await db.collection("Requests").get();
      const totalRequests = totalRequestsSnapshot.size;

      const acceptedRequestsSnapshot = await db
        .collection("Requests")
        .where("status", "==", "Accepted")
        .get();
      const acceptedCount = acceptedRequestsSnapshot.size;

      const rejectedRequestsSnapshot = await db
        .collection("Requests")
        .where("status", "==", "Rejected")
        .get();
      const rejectedCount = rejectedRequestsSnapshot.size;

      const pendingRequestsSnapshot = await db
        .collection("Requests")
        .where("status", "==", "Pending")
        .get();
      const pendingCount = pendingRequestsSnapshot.size;

      // Prepare the response
      const response = {
        totalRequests: totalRequests,
        acceptedCount: acceptedCount,
        rejectedCount: rejectedCount,
        pendingCount: pendingCount,
        acceptedPercentage: ((acceptedCount / totalRequests) * 100).toFixed(2),
        rejectedPercentage: ((rejectedCount / totalRequests) * 100).toFixed(2),
        pendingPercentage: ((pendingCount / totalRequests) * 100).toFixed(2),
      };

      return res.status(StatusCodes.OK).json(response);
    } else {
      return res
        .status(StatusCodes.FORBIDDEN)
        .send("You do not have permission to access this data");
    }
  } catch (error) {
    console.error("Error counting requests:", error);
    res.status(StatusCodes.INTERNAL_SERVER_ERROR).send({
      message: "Server error",
      error: error.message,
    });
  }
};

module.exports = { countAllRequests };
