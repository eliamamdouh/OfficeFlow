package com.example.project

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project.PreferencesManager.getTokenFromPreferences
import com.example.project.RetrofitClient.apiService
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

@Composable
fun NotificationPage() {
    val scrollState = rememberScrollState()
    var showScrollToTop by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    var notifications by remember { mutableStateOf<List<Notification>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val token =getTokenFromPreferences(context)
    println(token)
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val response = RetrofitClient.apiService.getNotifications("Bearer $token")
                if (response.isSuccessful) {
                    notifications = response.body() ?: emptyList()
                } else {
                    errorMessage = "Error fetching notifications: ${response.message()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            }
        }
    }


    LaunchedEffect(scrollState.value) {
        showScrollToTop = scrollState.value > 300
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
            .fillMaxHeight()
            .fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(35.dp))
                .padding(16.dp)
                .height(700.dp)
                .verticalScroll(scrollState)
        ) {
            Text(
                text = "Notifications",
                fontSize = 24.sp,
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 16.dp)
            )

            if (errorMessage != null) {
                Text(
                    text = errorMessage ?: "",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                notifications.forEach { notification ->
                    NotificationCard(
                        message = notification.text,
                        backgroundColor = Color(0xFFFEE8E8),
                        sidebarColor = Color(0xFFF44336),
                        iconResId = R.drawable.sa7


                    )
                }
            }
        }

        if (showScrollToTop) {
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        scrollState.scrollTo(0)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .size(80.dp)
                    .padding(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.arrowupgray),
                    contentDescription = "Scroll to Top",
                    modifier = Modifier
                        .size(40.dp)
                        .alpha(0.6f)
                )
            }
        }
    }
}

@Composable
fun NotificationCard(
    message: String,
    backgroundColor: Color,
    iconResId: Int,
    sidebarColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
    ) {
        Box(
            modifier = Modifier
                .width(8.dp)
                .background(sidebarColor)
                .size(100.dp)
        )
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = message,
                fontSize = 16.sp,
                color = Color.Black
            )
        }
    }
}
