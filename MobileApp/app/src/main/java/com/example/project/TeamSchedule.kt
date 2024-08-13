package com.example.project
//FIX PACKAGE


import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.project.components.CalendarContent
import com.example.project.components.CalendarView
import com.example.project.components.LegendItem
import kotlinx.coroutines.delay
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import com.example.project.ui.theme.DarkGrassGreen2
import com.example.project.ui.theme.DarkTeal2
import com.example.project.ui.theme.LightGrassGreen
//import com.google.android.gms.common.api.Response

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
                                itemList = teamMembers.map { it.name ?: "Unknown" },
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

@Composable
fun DropdownList(
    itemList: List<String>,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    onItemClick: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedItem = if (selectedIndex >= 0) itemList[selectedIndex] else "Select a team member"

    Box(modifier = modifier) {
        TextButton(
            onClick = { expanded = !expanded },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF6F6F6), RoundedCornerShape(8.dp)) // Background color
                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                .padding(3.dp)
        ) {
            Text(
                text = selectedItem,
                fontSize = 16.sp,
                color = if (selectedIndex >= 0) Color.Black else Color(0xFFBDBDBD), // Text color based on selection
                modifier = Modifier.weight(1f) // Ensure text takes up available space
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Dropdown Arrow",
                tint = LightGrassGreen, // Arrow color
                modifier = Modifier.size(34.dp) // Make the arrow slightly larger
            )
        }

        // Animated visibility for dropdown list
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(animationSpec = tween(durationMillis = 300)) + expandVertically(animationSpec = tween(durationMillis = 300)),
            exit = fadeOut(animationSpec = tween(durationMillis = 300)) + shrinkVertically(animationSpec = tween(durationMillis = 300))
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp) // Limit the height to show only four rows
                    .background(Color(0xFFF6F6F6)) // Background color of the dropdown list
                    .border(1.dp, Color.Gray)
                    .clip(RoundedCornerShape(8.dp)) // Rounded corners
            ) {
                items(itemList.size) { index ->
                    if (index != 0) {
                        Divider(thickness = 1.dp, color = Color.LightGray)
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onItemClick(index)
                                expanded = false
                            }
                            .padding(vertical = 8.dp) // Increased padding between items
                            .background(Color(0xFFF6F6F6)) // Background color for each item
                    ) {
                        Text(
                            text = itemList[index],
                            color = if (index == selectedIndex) Color.Black else Color(0xFFBDBDBD), // Black color for selected item
                            modifier = Modifier.padding(horizontal = 16.dp) // Padding inside each item
                        )
                    }
                }
            }
        }
    }
}
