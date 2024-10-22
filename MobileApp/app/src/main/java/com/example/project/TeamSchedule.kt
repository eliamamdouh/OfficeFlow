package com.example.project

import android.content.Context
import android.util.Base64
import android.util.Log
import android.widget.Toast
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
    var schedule by remember { mutableStateOf<Map<String, Map<String, List<ScheduleDay>>>?>(null) }
    var showSuperManagerOptions by remember { mutableStateOf(false) }
    var showManagerOptions by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<String?>(null) }
    var daysFromOfficeInOddWeeks by remember { mutableStateOf("") }
    var daysFromOfficeInEvenWeeks by remember { mutableStateOf("") }

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

    // Fetch the selected team member's schedule
    if (selectedTeamMemberId != null) {
        // Fetch the selected team member's schedule
        LaunchedEffect(selectedTeamMemberId) {
            Log.d("ScheduleScreen", "Employee ID: $selectedTeamMemberId")
            selectedTeamMemberId?.let {
                val call = RetrofitClient.apiService.viewScheduleForTeamMembers(
                    "Bearer $token",
                    selectedTeamMemberId!!
                )
                call.enqueue(object : Callback<ScheduleResponse> {
                    override fun onResponse(
                        call: Call<ScheduleResponse>,
                        response: Response<ScheduleResponse>
                    ) {
                        if (response.isSuccessful) {
                            schedule = response.body()?.schedule
                        } else {
                            println("Error fetching schedule: ${response.errorBody()?.string()}")
                        }
                    }

                    override fun onFailure(call: Call<ScheduleResponse>, t: Throwable) {
                        println("Failed to fetch schedule: ${t.message}")
                    }
                })
            }
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
                                    ){
                                        // Days from Office in Odd Weeks
                                        Text(
                                            "Days from Office in Odd Weeks:",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        OutlinedTextField(
                                            value = daysFromOfficeInOddWeeks,
                                            onValueChange = { newValue -> daysFromOfficeInOddWeeks = newValue },
                                            label = { Text("Enter number of days") },
                                            modifier = Modifier.fillMaxWidth()
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        // Days from Office in Even Weeks
                                        Text(
                                            "Days from Office in Even Weeks:",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        OutlinedTextField(
                                            value = daysFromOfficeInEvenWeeks,
                                            onValueChange = { newValue -> daysFromOfficeInEvenWeeks = newValue },
                                            label = { Text("Enter number of days") },
                                            modifier = Modifier.fillMaxWidth()
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))
                                        // Inside the if (showSuperManagerOptions) block
                                        Button(
                                            onClick = {
                                                // Create a request object for dynamic schedule generation
                                                val request = GenerateScheduleRequest(
                                                    oddWeekOfficeDays = daysFromOfficeInOddWeeks,
                                                    evenWeekOfficeDays = daysFromOfficeInEvenWeeks
                                                )

                                                if (token != null) {
                                                    // Log the request details if needed
                                                    Log.d("DynamicScheduleRequest", "Request: ${request}")

                                                    // Make the API call using Retrofit to generate the dynamic schedule
                                                    RetrofitClient.apiService.generateDynamicSchedule("Bearer $token", request)
                                                        .enqueue(object : Callback<GenerateScheduleResponse> {
                                                            override fun onResponse(call: Call<GenerateScheduleResponse>, response: Response<GenerateScheduleResponse>) {
                                                                if (response.isSuccessful) {
                                                                    val dynamicScheduleResponse = response.body()
                                                                    if (dynamicScheduleResponse != null) {
                                                                        // Handle successful schedule generation
                                                                        Toast.makeText(context, "Schedule updated successfully", Toast.LENGTH_LONG).show()
                                                                    } else {
                                                                        // Handle null response body
                                                                        Toast.makeText(context, "Failed to update schedule", Toast.LENGTH_LONG).show()
                                                                        Log.e("DynamicSchedule", "Response was null")
                                                                    }
                                                                } else {
                                                                    if (response.code() == 401) {
                                                                        Log.d("respCode:", "$response.code()")
                                                                        handleTokenExpiration(navController)
                                                                    } else {
                                                                        val errorMessage = response.errorBody()?.string() ?: "Failed to update schedule"
                                                                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                                                                        Log.e("DynamicSchedule", "Update failed: ${response.message()}, Raw error body: $errorMessage")
                                                                    }
                                                                }
                                                            }

                                                            override fun onFailure(call: Call<GenerateScheduleResponse>, t: Throwable) {
                                                                // Handle API call failure
                                                                Toast.makeText(context, "Failed to update schedule: ${t.message}", Toast.LENGTH_LONG).show()
                                                                Log.e("DynamicSchedule", "API call failed: ${t.message}")
                                                            }
                                                        })
                                                } else {
                                                    // Handle missing token
                                                    Toast.makeText(context, "Token is missing from SharedPreferences", Toast.LENGTH_LONG).show()
                                                    Log.e("DynamicSchedule", "Token is missing from SharedPreferences")
                                                }
                                            },
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
