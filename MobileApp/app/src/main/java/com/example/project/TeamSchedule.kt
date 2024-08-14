package com.example.project
//FIX PACKAGE


import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.project.components.CalendarContent
import com.example.project.components.DropdownList
import com.example.project.components.LegendItem
import kotlinx.coroutines.delay
import java.time.YearMonth
import com.example.project.ui.theme.DarkGrassGreen2
import com.example.project.ui.theme.DarkTeal2


import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



@Composable
fun ScheduleScreen(context: Context) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedTeamMemberId by remember { mutableStateOf<String?>(null) } // Stores the selected team member's userId
    var teamMembers by remember { mutableStateOf<List<TeamMember>>(emptyList()) } // List of team members
    var showPopup by remember { mutableStateOf(false) }
    var schedule by remember { mutableStateOf<Map<String, List<ScheduleDay>>?>(null) }

    // User Data
    val managerId: String? = remember(context) { PreferencesManager.getUserIdFromPreferences(context) }
    val token = PreferencesManager.getTokenFromPreferences(context)

    LaunchedEffect(managerId) {
        managerId?.let {
            if (token != null) {
                Log.d("ScheduleScreen", "Manager ID: $it") // Log the managerId for debugging
                val call = RetrofitClient.apiService.getTeamMembers("Bearer $token", managerId = it)
                call.enqueue(object : Callback<TeamMembersResponse> {
                    override fun onResponse(
                        call: Call<TeamMembersResponse>,
                        response: Response<TeamMembersResponse>
                    ) {
                        if (response.isSuccessful) {
                            teamMembers = response.body()?.teamMembers ?: emptyList()
                        } else {
                            println("Error fetching team members: ${response.errorBody()?.string()}")
                        }
                    }

                    override fun onFailure(call: Call<TeamMembersResponse>, t: Throwable) {
                        println("Failed to fetch team members: ${t.message}")
                    }
                })
            } else {
                Log.e("ScheduleScreen", "Token is missing from SharedPreferences")
            }
        }
    }

    // Fetch the selected team member's schedule
    LaunchedEffect(selectedTeamMemberId) {
        selectedTeamMemberId?.let { memberId ->
            val call = RetrofitClient.apiService.viewSchedule(memberId)
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F6F6)) // Screen background grey
            .padding(top = 20.dp, bottom = 100.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp) // Padding on left and right
                .background(Color.White, RoundedCornerShape(36.dp)) // Box background white
                .padding(16.dp) // Internal padding
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
                                itemList = teamMembers.map { it.name },
                                selectedIndex = teamMembers.indexOfFirst { it.userId == selectedTeamMemberId },
                                onItemClick = { selectedTeamMemberId = teamMembers[it].userId },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp)
                            )

                            if (selectedTeamMemberId != null && schedule != null) {
                                Text(
                                    text = "Click on day to change between office and home",
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
                                    onDateSelected = {},
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