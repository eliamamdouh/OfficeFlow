const express = require('express');
const router = express.Router();
const { createUser,loginUser } = require('../Controllers/userController');


router.post('/create', createUser);
router.post('/login',loginUser)

module.exports = router;
