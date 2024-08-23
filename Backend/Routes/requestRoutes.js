const express = require("express");
const router = express.Router();

// Import necessary controllers
const { submitRequest } = require("../Controllers/submitRequestContoller");
const { getRequests } = require("../Controllers/getRequests");
const { countAllRequests } = require("../Controllers/countAllRequests");

// Define routes
router.post("/submit-request", submitRequest);
router.get("/view-requests", getRequests);
router.get("/countRequests", countAllRequests);

// Export the router
module.exports = router;
