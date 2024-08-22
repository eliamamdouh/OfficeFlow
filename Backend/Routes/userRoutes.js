const express = require("express");
const router = express.Router();

const { createUser, login, getUserInfo, getTeamMembers } = require('../Controllers/userController');
const { submitRequest } = require('../Controllers/submitRequestContoller');
const { viewSchedule } = require('../Controllers/Schedule/viewSchedule');
const { viewRequests } = require('../Controllers/viewRequestsController');
const { cancelRequest } = require ('../Controllers/cancelRequest');
const { acceptRequest } = require('../Controllers/acceptRequest');
const { rejectRequest } = require('../Controllers/rejectRequest');
const {generateDynamicSchedule, clearAllUserSchedules} = require('../Controllers/dynamicSchedule')



router.post('/create', createUser);
router.post('/login', login);
router.get('/schedule', viewSchedule);
router.post('/submit-request', submitRequest);
router.get('/view-requests', viewRequests);  // Moved here
router.get('/:userId', getUserInfo);
router.post('/cancel-request', cancelRequest);
router.post('/accept-request', acceptRequest);
router.post('/reject-request', rejectRequest);
router.post('/generate-dynamicSchedule', generateDynamicSchedule);
router.post('/clear-schedules', clearAllUserSchedules);

module.exports = router;
