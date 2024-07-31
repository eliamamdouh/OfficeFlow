const express = require('express');
const router = express.Router();
const { createUser } = require('../Controllers/userController');

// Create user route
router.post('/create', createUser);

module.exports = router;
