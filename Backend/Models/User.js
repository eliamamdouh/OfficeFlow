const mongoose = require('mongoose');

const userSchema = new   mongoose.Schema({
    username: {
        type: String,
        required: [true, 'Please enter a username']
    },
    password: {
        type: String,
        required: [true,'Please enter a password' ]
    },

    email :{
        type: String
    },
    userId:{
        type: String
    },
    managerId:{
        type: String
    },
},
    {timestamps: true});




 
const User = mongoose.model('User', userSchema);
module.exports = User;