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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun NotificationPage() {
    val scrollState = rememberScrollState()
    var showScrollToTop by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Update the visibility of the "Scroll to Top" button based on scroll position
    LaunchedEffect(scrollState.value) {
        showScrollToTop = scrollState.value > 300 // You can adjust this threshold
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
                .verticalScroll(scrollState) // Make column scrollable
        ) {
            Text(
                text = "Notifications",
                fontSize = 24.sp,
                color = Color.Black,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 16.dp)
            )
            NotificationCard(
                message = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec fringilla quam eu facilisis mollis.",
                backgroundColor = Color(0xFFE8F8F8),
                iconResId = R.drawable.sa7,
                sidebarColor = Color(0xFF4CAF50)
            )
            NotificationCard(
                message = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec fringilla quam eu facilisis mollis.",
                backgroundColor = Color(0xFFFEE8E8),
                iconResId = R.drawable.ghalat,
                sidebarColor = Color(0xFFF44336)
            )
            NotificationCard(
                message = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec fringilla quam eu facilisis mollis.",
                backgroundColor = Color(0xFFE8F8F8),
                iconResId = R.drawable.sa7,
                sidebarColor = Color(0xFF4CAF50)
            )
            NotificationCard(
                message = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec fringilla quam eu facilisis mollis.",
                backgroundColor = Color(0xFFFEE8E8),
                iconResId = R.drawable.ghalat,
                sidebarColor = Color(0xFFF44336)
            )
            NotificationCard(
                message = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec fringilla quam eu facilisis mollis.",
                backgroundColor = Color(0xFFE8F8F8),
                iconResId = R.drawable.sa7,
                sidebarColor = Color(0xFF4CAF50)
            )
            NotificationCard(
                message = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec fringilla quam eu facilisis mollis.",
                backgroundColor = Color(0xFFFEE8E8),
                iconResId = R.drawable.ghalat,
                sidebarColor = Color(0xFFF44336)
            )
            NotificationCard(
                message = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec fringilla quam eu facilisis mollis.",
                backgroundColor = Color(0xFFE8F8F8),
                iconResId = R.drawable.sa7,
                sidebarColor = Color(0xFF4CAF50)
            )
            NotificationCard(
                message = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec fringilla quam eu facilisis mollis.",
                backgroundColor = Color(0xFFFEE8E8),
                iconResId = R.drawable.ghalat,
                sidebarColor = Color(0xFFF44336)
            )
            // Add more NotificationCard instances as needed
        }

        // Scroll to Top Button
        if (showScrollToTop) {
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        scrollState.scrollTo(0)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)//BottomEnd
                    .size(80.dp)
                    .padding(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.arrowupgray), // Replace with your icon resource ID
                    contentDescription = "Scroll to Top",
                    modifier = Modifier.size(40.dp)
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
