const express = require('express');
const router = express.Router();
const { createUser,loginUser } = require('../Controllers/userController');
const {getWorkingDays} = require('../Controllers/workingDays')


router.post('/create', createUser);
router.post('/login',loginUser)
router.get('/getWorkingDays',getWorkingDays)


module.exports = router;
