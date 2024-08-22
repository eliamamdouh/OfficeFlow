package com.example.project

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.project.components.CalendarContent
import com.example.project.components.DropdownList
import com.example.project.components.LegendItem
import com.example.project.components.parseUserIdFromToken
import com.example.project.ui.theme.BackgroundGray
import com.example.project.ui.theme.DarkGrassGreen2
import com.example.project.ui.theme.DarkTeal2
import kotlinx.coroutines.delay
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.YearMonth

// Function to parse email from JWT token
fun parseEmailFromToken(token: String): String? {
    return try {
        val parts = token.split(".")
        if (parts.size == 3) {
            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
            val jsonObject = JSONObject(payload)
            jsonObject.getString("email") // Assumes the token has an "email" field in the payload
        } else {
            null
        }
    } catch (e: Exception) {
        null
    }
}

@Composable
fun ScheduleScreen(context: Context, navController: NavController) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedTeamMemberId by remember { mutableStateOf<String?>(null) }
    var teamMembers by remember { mutableStateOf<List<TeamMember>>(emptyList()) }
    var showPopup by remember { mutableStateOf(false) }
    var schedule by remember { mutableStateOf<Map<String, List<ScheduleDay>>?>(null) }
    var showSuperManagerOptions by remember { mutableStateOf(false) }
    var showManagerOptions by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<String?>(null) }

    // User Data
    val token = PreferencesManager.getTokenFromPreferences(context)
    val managerId = token?.let { parseUserIdFromToken(it) }
    val email = token?.let { parseEmailFromToken(it) }

    // Check if the logged-in user is the SuperManager
    LaunchedEffect(email) {
        if (email == "SuperManager@Deloitte.com") {
            showSuperManagerOptions = true
        }
    }

    LaunchedEffect(managerId) {
        managerId?.let {
            Log.d("ScheduleScreen", "Manager ID: $it")
            val call = RetrofitClient.apiService.getTeamMembers("Bearer $token")
            call.enqueue(object : Callback<TeamMembersResponse> {
                override fun onResponse(
                    call: Call<TeamMembersResponse>,
                    response: Response<TeamMembersResponse>
                ) {
                    if (response.isSuccessful) {
                        teamMembers = response.body()?.teamMembers ?: emptyList()
                    } else {
                        if (response.code() == 401) {
                            Log.d("respCode:", "$response.code()")
                            handleTokenExpiration(navController)
                        } else {
                            println("Error fetching team members: ${response.errorBody()?.string()}")
                        }
                    }
                }

                override fun onFailure(call: Call<TeamMembersResponse>, t: Throwable) {
                    println("Failed to fetch team members: ${t.message}")
                }
            })
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
            .padding(top = 20.dp, bottom = 100.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp)
                .background(Color.White, RoundedCornerShape(36.dp))
                .padding(16.dp)
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Column {
                            Text(
                                text = "Team Schedule",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier
                                    .padding(bottom = 18.dp)
                                    .align(Alignment.CenterHorizontally)
                            )

                            DropdownList(
                                itemList = teamMembers.map { it.name ?: "Unknown" },
                                selectedIndex = teamMembers.indexOfFirst { it.userId == selectedTeamMemberId },
                                onItemClick = { selectedTeamMemberId = teamMembers[it].userId },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp)
                            )

                            if (showSuperManagerOptions) {
                                Button(
                                    onClick = { showManagerOptions = !showManagerOptions },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF4CAF50) // Green color for the button
                                    )
                                ) {
                                    Text(text = "Change Schedule Algorithm", color = Color.White)
                                }

                                if (showManagerOptions) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    ) {
                                        // Fields for Days from Home and Days from Work
                                        Text(
                                            "Days from Home:", fontSize = 16.sp, fontWeight = FontWeight.Bold
                                        )
                                        OutlinedTextField(
                                            value = "",
                                            onValueChange = { /* Handle input */ },
                                            label = { Text("Enter number of days") },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            "Days from Work:", fontSize = 16.sp, fontWeight = FontWeight.Bold
                                        )
                                        OutlinedTextField(
                                            value = "",
                                            onValueChange = { /* Handle input */ },
                                            label = { Text("Enter number of days") },
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Button(
                                            onClick = { /* Handle save changes logic */ },
                                            modifier = Modifier
                                                .align(Alignment.CenterHorizontally)
                                                .padding(vertical = 8.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF4CAF50) // Green color for the button
                                            )
                                        ) {
                                            Text("Save Changes", color = Color.White) // White text color
                                        }
                                    }
                                }
                            }

                            if (selectedTeamMemberId != null && schedule != null) {
                                Text(
                                    text = "Working from home/office schedule",
                                    fontFamily = FontFamily.SansSerif,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                                Divider(
                                    color = Color.Gray,
                                    thickness = 1.dp,
                                    modifier = Modifier
                                        .padding(vertical = 8.dp)
                                        .alpha(0.3f)
                                )

                                // Display schedule in the calendar
                                CalendarContent(
                                    context = context,
                                    userId = selectedTeamMemberId!!,
                                    onDateSelected = { selectedDate = it.toString() },
                                    showMonthNavigation = true,
                                    onPreviousMonth = { currentMonth = currentMonth.minusMonths(1) },
                                    onNextMonth = { currentMonth = currentMonth.plusMonths(1) },
                                    selectedDate = null,
                                    restrictDateSelection = false,
                                    isDialog = false
                                )

                                Divider(
                                    color = Color.Gray,
                                    thickness = 1.dp,
                                    modifier = Modifier
                                        .padding(vertical = 34.dp)
                                        .alpha(0.3f)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    LegendItem(color = DarkTeal2, label = "From Office")
                                    LegendItem(color = DarkGrassGreen2, label = "From Home")
                                }

                                LaunchedEffect(Unit) {
                                    delay(3000)
                                    showPopup = false
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
