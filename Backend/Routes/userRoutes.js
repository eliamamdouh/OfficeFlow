const express = require('express');
const router = express.Router();
const { createUser,loginUser,getUserInfo } = require('../Controllers/userController');
const {submitRequest} = require('../Controllers/submitRequestContoller')


router.post('/create', createUser);
router.post('/login',loginUser)
router.post('/submit-request', submitRequest)
router.get('/:userId',getUserInfo)

module.exports = router;
