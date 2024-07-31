package com.example.project


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController



@Composable
fun LoginScreen(navController: NavHostController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background image
        BackgroundImage()

        // Foreground content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.offset(y = 20.dp)

                ) {
                    // Logo
                    Logo(
                        modifier = Modifier.size(250.dp)
                            .offset(x = 0.dp, y = (-90).dp)
                    )

                    Text(
                        text = stringResource(id = R.string.login),
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.offset(0.dp, (-160).dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))//remove
                }
            }


            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.offset(y = (-140).dp)
                // Adjust this offset to raise the column as needed
            ) {

                TextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text(text = "Email Address",color = Color.Gray.copy(alpha = 0.7f)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                        .padding(horizontal = 15.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .height(45.dp),
                    singleLine = true,
                    textStyle = TextStyle(color = Color.White.copy(alpha = 0.7f))  // Adjust alpha value for transparency
                )

                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(
                        text = "Password",//STARSSS
                        color = Color.Gray.copy(alpha = 0.7f), // Adjust alpha for transparency
                    ) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                        .padding(horizontal = 15.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .height(45.dp),
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image =
                            if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        val description = if (passwordVisible) "Hide password" else "Show password"
                        val iconColor = if (passwordVisible) Color(0xFFB0D173) else Color.Gray

                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = image,
                                contentDescription = description,
                                tint = iconColor
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                )


                Button(
                    onClick = { navController.navigate("page1") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF86BC24)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                        .padding(horizontal = 15.dp)
                        .clip(RoundedCornerShape(6.dp)),
                ) {
                    Text(
                        text = "Login",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }

        }
    }
}

@Composable
fun Logo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.logo4),
        contentDescription = "",
        modifier = modifier
    )
}

@Composable
fun BackgroundImage() {
    Image(
        painter = painterResource(id = R.drawable.splash1),
        contentDescription = "",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )
}
