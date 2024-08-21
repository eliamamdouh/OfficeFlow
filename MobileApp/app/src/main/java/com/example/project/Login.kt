package com.example.project

import android.util.Log
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
import androidx.compose.ui.platform.LocalContext
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
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@Composable
fun LoginScreen(navController: NavHostController, onLoginSuccess: (String) -> Unit) {
    var passwordVisible by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var loginError by remember { mutableStateOf<String?>(null) }
    var deviceToken by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            deviceToken = task.result
            Log.d("test", "FCM Token successfully retrieved: $deviceToken")
        } else {
            Log.w("test", "Fetching FCM registration token failed", task.exception)
        }
    }.addOnFailureListener { exception ->
        Log.e("test", "Error retrieving FCM token", exception)
    }

    LoginScreenContent(
        email = email,
        onEmailChange = {
            email = it
            val isEmailValid = it.lowercase().contains("@deloitte.com")
            emailError = if (isEmailValid) null else "Email must include @deloitte.com"
            loginError = null
        },
        password = password,
        onPasswordChange = {
            password = it
            loginError = null
        },
        passwordVisible = passwordVisible,
        onPasswordVisibilityChange = { passwordVisible = !passwordVisible },
        emailError = emailError,
        loginError = loginError,
        isFormValid = email.isNotBlank() && password.isNotBlank() && email.lowercase().contains("@deloitte.com"),
        onLoginClick = {
            if (email.isNotBlank() && password.isNotBlank() && email.lowercase().contains("@deloitte.com")) {
                val request = LoginRequest(email, password, deviceToken)
                //val request = LoginRequest(email.lowercase(), password, deviceToken)
                RetrofitClient.apiService.loginUser(request).enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if (response.isSuccessful) {
                            val loginResponse = response.body()

                            if (loginResponse != null) {
                                Log.d("LOGIN" , loginResponse.role)
                            }

                            if (loginResponse != null && loginResponse.userId.isNotEmpty()) {
                                PreferencesManager.saveUserIdToPreferences(context, loginResponse.userId)
                                PreferencesManager.saveTokenToPreferences(context, loginResponse.token)

                                // Notify AppNavigation of the role
                                onLoginSuccess(loginResponse.role)
                            } else {
                                loginError = "Unexpected response from the server"
                            }
                        } else {
                            loginError = when (response.code()) {
                                401 -> "Invalid email or password"
                                else -> "Login failed: ${response.message()}"
                            }
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        loginError = "API call failed: ${t.message}"
                    }
                })
            }
        }
    )
}

@Composable
fun LoginScreenContent(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibilityChange: () -> Unit,
    emailError: String?,
    loginError: String?,
    isFormValid: Boolean,
    onLoginClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        BackgroundImage()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LogoHeader()

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.offset(y = (-140).dp)
            ) {
                CustomTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    placeholder = "Email Address",
                    isError = emailError != null
                )
                emailError?.let {
                    ErrorMessage(text = it)
                }

                CustomTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    placeholder = "Password",
                    visualTransformation = if (passwordVisible) VisualTransformation.None else AsteriskVisualTransformation(),
                    trailingIcon = {
                        IconButton(
                            onClick = onPasswordVisibilityChange,
                            enabled = password.isNotBlank()
                        ) {
                            val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            val tint = if (passwordVisible) Color(0xFF86BC24) else Color.Gray
                            Icon(imageVector = icon, contentDescription = null, tint = tint)
                        }
                    }
                )
                loginError?.let {
                    ErrorMessage(text = it)
                }

                LoginButton(
                    isFormValid = isFormValid,
                    onClick = onLoginClick
                )
            }
        }
    }
}

@Composable
fun LogoHeader() {
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
            Logo(
                modifier = Modifier.size(250.dp).offset(y = (-90).dp)
            )
            Text(
                text = stringResource(id = R.string.login),
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.offset(y = (-160).dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
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
        textStyle = TextStyle(fontSize = 16.sp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        isError = isError,
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon
    )
}

@Composable
fun ErrorMessage(text: String) {
    Text(
        text = text,
        color = Color.Red,
        style = TextStyle(fontSize = 18.sp),
        modifier = Modifier.padding(vertical = 10.dp)
    )
}

@Composable
fun LoginButton(
    isFormValid: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
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


class AsteriskVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val transformedText = text.text.map { '*' }.joinToString("")
        return TransformedText(AnnotatedString(transformedText), OffsetMapping.Identity)
    }
}
