const express = require('express');
const router = express.Router();
const { createUser,loginUser,getUserInfo } = require('../Controllers/userController');


router.post('/create', createUser);
router.post('/login',loginUser)
router.get('/:userId',getUserInfo)

module.exports = router;
