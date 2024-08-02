const express = require('express');
const router = express.Router();
const { createUser,loginUser } = require('../Controllers/userController');
const { viewSchedule } = require('../Controllers/viewSchedule');


router.post('/create', createUser);
router.post('/login',loginUser);
router.get('/schedule', viewSchedule);

module.exports = router;
