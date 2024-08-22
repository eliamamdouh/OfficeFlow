package com.example.project

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun AnalyticsScreen(navController: NavController) {
    var officeCapacity by remember { mutableStateOf<Int?>(null) }
    val currentDate = getCurrentDate() // Function to get the current date as a string
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(currentDate) {
        coroutineScope.launch {
            officeCapacity = fetchOfficeCapacity(currentDate)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
        ) {
            Text(
                text = "Office Capacity Analysis",
                fontSize = 24.sp,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )

            // Display Office Capacity
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFD6D6D6),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(16.dp)
            ) {
                Text(
                    text = "Office Capacity Today: ${officeCapacity ?: "Loading..."} people",
                    fontSize = 16.sp,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Other analytics can go here...
            Button(onClick = { /* Add more analytics actions here */ }) {
                Text(text = "View More Analytics")
            }
        }
    }
}

suspend fun fetchOfficeCapacity(date: String): Int? {
    return try {
        val db = FirebaseFirestore.getInstance()
        val capacityDocRef = db.collection("OfficeCapacity").document(date).get().await()
        if (capacityDocRef.exists()) {
            capacityDocRef.getLong("count")?.toInt()
        } else {
            0 // Return 0 if the document doesn't exist
        }
    } catch (e: Exception) {
        Log.e("fetchOfficeCapacity", "Error fetching capacity: ${e.message}")
        null
    }
}

fun getCurrentDate(): String {
    // Get the current date
    val currentDate = LocalDate.now()
    // Format the date to "yyyy-MM-dd"
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return currentDate.format(formatter)
}