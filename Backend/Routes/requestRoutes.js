const express = require('express');
const router = express.Router();

// Import necessary controllers
const { submitRequest } = require('../Controllers/submitRequestContoller');
const { getRequests } = require('../Controllers/getRequests');

// Define routes
router.post('/submit-request', submitRequest);
router.get('/view-requests', getRequests);

// Export the router
module.exports = router;