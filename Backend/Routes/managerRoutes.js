const express = require('express');
const router = express.Router();
const {createManager,loginManager,checkEmail} =require('../Controllers/managerController')

router.post('/createManager', createManager)
router.post('/loginManager', loginManager)
router.post('/checkemail',checkEmail)


module.exports = router;