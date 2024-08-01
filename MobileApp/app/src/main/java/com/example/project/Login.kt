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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText


@Composable
fun LoginScreen(navController: NavHostController) {
    var passwordVisible by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }

    // Check if the email is valid and set the error message if not
    val isEmailValid = email.contains("@deloitte.com")
    val isFormValid = email.isNotBlank() && password.isNotBlank() && isEmailValid

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
                    Spacer(modifier = Modifier.height(16.dp)) //remove
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.offset(y = (-140).dp)
                // Adjust this offset to raise the column as needed
            ) {
                TextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = if (it.contains("@deloitte.com")) null else "Email must include @deloitte.com"
                    },
                    placeholder = {
                        Text(
                            text = "Email Address",
                            color = Color.Gray.copy(alpha = 0.7f)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                        .padding(horizontal = 15.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .height(50.dp),
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    isError = emailError != null
                )

                if (emailError != null) {
                    Text(
                        text = emailError!!,
                        color = Color.Red,
                        style = TextStyle(fontSize = 18.sp),
                        //modifier = Modifier.padding(horizontal = 15.dp)
                    )
                }

                TextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = {
                        Text(
                            text = "Password",
                            color = Color.Gray.copy(alpha = 0.7f)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                        .padding(horizontal = 15.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .height(50.dp),
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else AsteriskVisualTransformation(),
                    trailingIcon = {
                        IconButton(
                            onClick = { passwordVisible = !passwordVisible },
                            enabled = password.isNotBlank()
                        ) {
                            val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            val tint = if (passwordVisible) Color(0xFF86BC24) else Color.Gray // Green when clicked
                            Icon(imageVector = icon, contentDescription = null, tint = tint)
                        }
                    }
                )

                Button(
                    onClick = { if (isFormValid) navController.navigate("page1") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFormValid) Color(0xFF86BC24) else Color(0xFFC7C7C7), // Gray color when disabled
                        disabledContainerColor = Color(0xFFC7C7C7) // Ensure this is set for disabled state
                    ),
                    enabled = isFormValid,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                        .padding(horizontal = 15.dp)
                        .clip(RoundedCornerShape(6.dp))
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

class AsteriskVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val transformedText = text.text.map { '*' }.joinToString("")
        return TransformedText(AnnotatedString(transformedText), OffsetMapping.Identity)
    }
}
