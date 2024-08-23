package com.example.project

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.project.components.parseUserIdFromToken
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun AnalyticsScreen(context: Context, navController: NavController) {
    var officeCapacity by remember { mutableStateOf<Int?>(null) }
    var countRequestsResponse by remember { mutableStateOf<CountRequestsResponse?>(null) }
    var selectedTab by remember { mutableStateOf("Accepted") }
    val currentDate = getCurrentDate()
    val coroutineScope = rememberCoroutineScope()
    val token = PreferencesManager.getTokenFromPreferences(context)
    val managerId = token?.let { parseUserIdFromToken(it) }

    LaunchedEffect(currentDate) {
        coroutineScope.launch {
            // Fetch office capacity
            officeCapacity = fetchOfficeCapacity(currentDate)
        }
    }

    LaunchedEffect(managerId) {
        managerId?.let {
            Log.d("AnalyticsScreen", "Manager ID: $it")
            val call = RetrofitClient.apiService.countRequests("Bearer $token")
            call.enqueue(object : Callback<CountRequestsResponse> {
                override fun onResponse(
                    call: Call<CountRequestsResponse>,
                    response: Response<CountRequestsResponse>
                ) {
                    if (response.isSuccessful) {
                        countRequestsResponse = response.body()
                    } else {
                        if (response.code() == 401) {
                            Log.d("respCode:", "$response.code()")
                            handleTokenExpiration(navController)
                        } else {
                            println("Error fetching request counts: ${response.errorBody()?.string()}")
                        }
                    }
                }

                override fun onFailure(call: Call<CountRequestsResponse>, t: Throwable) {
                    println("Failed to fetch request counts: ${t.message}")
                }
            })
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

            // Tab Layout for Accepted, Pending, Rejected
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("Accepted", "Pending", "Rejected").forEach { tab ->
                    Button(
                        onClick = { selectedTab = tab },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedTab == tab) Color(0xFF0041C4) else Color(0xFFB0C4DE),
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = tab)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bar Chart Display
            countRequestsResponse?.let {
                val totalRequests = it.totalRequests.toFloat()
                val acceptedPercentage = (it.acceptedCount / totalRequests) * 100
                val pendingPercentage = (it.pendingCount / totalRequests) * 100
                val rejectedPercentage = (it.rejectedCount / totalRequests) * 100

                val selectedPercentage = when (selectedTab) {
                    "Accepted" -> acceptedPercentage
                    "Pending" -> pendingPercentage
                    "Rejected" -> rejectedPercentage
                    else -> 0f
                }

                // Bar chart
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .background(Color(0xFFD6D6D6), shape = RoundedCornerShape(12.dp))
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(selectedPercentage / 100)
                            .background(
                                color = when (selectedTab) {
                                    "Accepted" -> Color(0xFF4CAF50) // Green
                                    "Pending" -> Color(0xFF9E9E9E)  // Gray
                                    "Rejected" -> Color(0xFFF44336) // Red
                                    else -> Color.Transparent
                                },
                                shape = RoundedCornerShape(12.dp)
                            )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${selectedPercentage.toInt()}% ${selectedTab} Requests",
                    fontSize = 16.sp,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}


@Composable
fun BarInfographic(acceptedPercentage: Float, pendingPercentage: Float, rejectedPercentage: Float) {
    // Logic to draw the bar infographic
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(24.dp)
            .background(Color.Gray, shape = RoundedCornerShape(12.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(acceptedPercentage)
                .background(Color.Green)
        )
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(pendingPercentage)
                .background(Color.Gray)
        )
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(rejectedPercentage)
                .background(Color.Red)
        )
    }
}

fun calculatePercentage(part: Int, total: Int): Float {
    return if (total > 0) (part.toFloat() / total) else 0f
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
    val currentDate = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return currentDate.format(formatter)
}
