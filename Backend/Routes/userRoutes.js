const express = require('express');
const router = express.Router();

const { createUser,loginUser } = require('../Controllers/userController');
const {submitRequest} = require('../Controllers/submitRequestContoller');


router.post('/create', createUser);
router.post('/login',loginUser)
router.post('/submit-request', submitRequest)

module.exports = router;
