package com.example.project
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project.ui.theme.ProjectTheme
import kotlin.math.round


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectTheme {

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ){
                    Box(modifier = Modifier.fillMaxSize()) {
                        BackgroundImage()
                        Logo(modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(30.dp, 60.dp)
                            .size(200.dp))
                        LoginScreen()

                    }


                }
            }
        }
    }
}
@Composable
fun BackgroundImage() {
    Image(painter = painterResource(id = R.drawable.splash1),contentDescription = "",
        contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize()
    ) }

//@Composable
//fun LoginText() {
//    Box(
//        modifier = Modifier.fillMaxSize()
//    ) {
//        Text(
//            text = stringResource(id = R.string.login),
//            color = Color.White,
//            fontSize = 28.sp,
//            fontWeight = FontWeight.Bold,
//            modifier = Modifier
//                .align(Alignment.TopCenter) // Change this to your desired position
//                .padding(top = 280.dp) // Adjust padding if needed
//        )
//    }
//}
//
//@Composable
//fun Username(){
//    var userInput by remember{
//        mutableStateOf("")
//
//    }
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp), // Adjust padding as needed
//        verticalArrangement = Arrangement.Center, // Change to position the TextField vertically
//        horizontalAlignment = Alignment.CenterHorizontally // Change to position horizontally
//    ){
//
//    TextField(value = userInput, onValueChange = {
//        userInput= it
//    }, label ={
//        Text(text = "Enter your username")
//    }
//    )
//}}
//
//@Composable
//fun Password(){
//    var userInput by remember{
//        mutableStateOf("")
//
//    }
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp), // Adjust padding as needed
//        verticalArrangement = Arrangement.Center, // Change to position the TextField vertically
//        horizontalAlignment = Alignment.CenterHorizontally // Change to position horizontally
//    ){
//
//        TextField(value = userInput, onValueChange = {
//            userInput= it
//        }, label ={
//            Text(text = "Enter your password")
//        }
//        )
//    }}

@Composable
fun LoginScreen() {
    // Remember user inputs for both username and password
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), // Adjust padding as needed
        verticalArrangement = Arrangement.Center, // Center the content vertically
        horizontalAlignment = Alignment.CenterHorizontally // Center the content horizontally
    ) {
        // Username Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp) // Space between username and password boxes
        ) {
            Text(
                text = stringResource(id = R.string.login),
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 20.dp) // Adjust padding to position text
            )
        }

        // Username TextField
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(text = "Enter your username") },
            shape = RoundedCornerShape(30.dp),

            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            singleLine = true // Adjust padding as needed
        )

        // Password TextField
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Enter your password") },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(30.dp))
                .padding(vertical = 8.dp),
            singleLine = true, // Adjust padding as needed
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                val description = if (passwordVisible) "Hide password" else "Show password"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = description)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        // Log In Button
        Button(
            onClick = { /* Handle login action here */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Green, // Green background color
                contentColor = Color.White // White text color
            )
        ) {
            Text(text = "Log In")
        }
    }
}


@Composable

fun Logo (modifier: Modifier =Modifier){
    Image(painter = painterResource(id = R.drawable.logo4),contentDescription="", modifier= modifier
    )
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ProjectTheme {
        Greeting("Android")
    }
}