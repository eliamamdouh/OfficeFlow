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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController


import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import androidx.compose.ui.platform.LocalContext
import android.content.Context
import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.graphicsLayer
import com.example.project.PreferencesManager
import coil.compose.rememberImagePainter
import coil.decode.GifDecoder
import coil.request.ImageRequest
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {
    var startAnimation by remember { mutableStateOf(false) }
    val gifOpacity by animateFloatAsState(
        targetValue = if (startAnimation) 0f else 1f,
        animationSpec = tween(durationMillis = 1000)
    )

    LaunchedEffect(Unit) {
        delay(1000) // Short delay before starting the animation
        startAnimation = true
        delay(1000) // Delay before navigating to login screen
        navController.navigate("page0")
    }

    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundImage()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GifImage(drawableResId = R.drawable.loading, opacity = gifOpacity)

            Spacer(modifier = Modifier.height(16.dp))
            AnimatedLogo(startAnimation)
        }
    }
}

@Composable
fun GifImage(drawableResId: Int, opacity: Float) {
    val context = LocalContext.current
    val painter = rememberImagePainter(
        ImageRequest.Builder(context)
            .data(drawableResId)
            .decoderFactory(GifDecoder.Factory())
            .build()
    )

    Image(
        painter = painter,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(200.dp)
            .border(4.dp, Color.Black)
            .graphicsLayer(alpha = opacity) // Apply opacity to the GIF
    )
}

@Composable
fun AnimatedLogo(startAnimation: Boolean) {
    val offsetY by animateDpAsState(
        targetValue = if (startAnimation) (-307).dp else -95.dp,
        animationSpec = tween(durationMillis = 1000),
        label = "LogoAnimation"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.offset(y = offsetY)
    ) {
        Logo(modifier = Modifier.size(250.dp))
        Spacer(modifier = Modifier.height(16.dp))
    }
}
@Composable
fun LoginScreen(navController: NavHostController) {
    var passwordVisible by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }

    // Check if the email is valid and set the error message if not

    val isEmailValid = email.contains("@Deloitte.com")

    val isFormValid = email.isNotBlank() && password.isNotBlank() && isEmailValid

    val context = LocalContext.current

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

                        emailError = if (it.contains("@Deloitte.com")) null else "Email must include @Deloitte.com"

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
                    onClick = {
                        if (isFormValid) {
                            // Create a LoginRequest object with email and password
                            val request = LoginRequest(email, password)

                            // Make the API call using Retrofit
                            RetrofitClient.apiService.loginUser(request).enqueue(object : Callback<LoginResponse> {
                                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                                    if (response.isSuccessful) {
                                        val loginResponse = response.body()
                                        if (loginResponse != null && loginResponse.userId.isNotEmpty()) {
                                            Log.d("LoginButton", "Login successful: ${loginResponse.userId}")
                                            println(loginResponse.role)
                                            // Save the userId in SharedPreferences
                                            PreferencesManager.saveUserIdToPreferences(context, loginResponse.userId)
                                            PreferencesManager.saveTokenToPreferences(context, loginResponse.token)
                                            PreferencesManager.saveRoleToPreferences(context, loginResponse.role)

                                            // Navigate to HomeScreen
                                            // removes the login screen from the back stack to prevent the user from returning to it
                                            navController.navigate("page1") {
                                                popUpTo("page0") { inclusive = true }
                                            }
                                        } else {
                                            Log.e("LoginButton", "Login response was null or userId was missing")
                                        }
                                    } else {
                                        Log.e("LoginButton", "Login failed: ${response.message()}")
                                    }
                                }
                                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                                    Log.e("LoginButton", "API call failed: ${t.message}")
                                }
                            })
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFormValid) Color(0xFF86BC24) else Color(0xFFC7C7C7),
                        disabledContainerColor = Color(0xFFC7C7C7)
                    ),
                    enabled = isFormValid,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                        .padding(horizontal = 15.dp)
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
