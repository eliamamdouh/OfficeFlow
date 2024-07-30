const { userModel } = require('../Models/User');

const createUserController = async (req, res) => {
    try {
        const userData = req.body; 
        const user = await userModel(userData);
        res.status(201).json({
            message: 'User created successfully',
            user
        });
    } catch (error) {
        res.status(500).json({
            message: 'Error creating user',
            error: error.message
        });
    }
};

module.exports = {
    createUserController 
};


