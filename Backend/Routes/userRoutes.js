const express = require('express');
const router = express.Router();
const { createUser,login,getUserInfo,getTeamMembers } = require('../Controllers/userController');
const {submitRequest} = require('../Controllers/submitRequestContoller')
const { viewSchedule } = require('../Controllers/viewSchedule');


router.post('/create', createUser);
router.post('/login',login);
router.get('/schedule', viewSchedule);
router.post('/submit-request', submitRequest);
router.get('/:userId',getUserInfo);

module.exports = router;
