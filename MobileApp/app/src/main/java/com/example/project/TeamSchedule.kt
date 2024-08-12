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
            Log.d("ScheduleScreen", "Manager ID: $it") // Log the managerId for debugging
            val call = RetrofitClient.apiService.getTeamMembers(managerId = it)
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
@SuppressLint("InvalidColorHexValue")
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
                tint = LightGrassGreen,// Color(0xFF86BC24), // Arrow color
                modifier = Modifier.size(34.dp) // Make the arrow slightly larger
            )
        }

        // Animated visibility for dropdown list
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn(animationSpec = tween(durationMillis = 300)) + expandVertically(animationSpec = tween(durationMillis = 300)),
            exit = fadeOut(animationSpec = tween(durationMillis = 300)) + shrinkVertically(animationSpec = tween(durationMillis = 300))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(Color(0xFFF6F6F6)) // Background color of the dropdown list
                    .border(1.dp, Color.Gray)
                    .clip(RoundedCornerShape(8.dp)) // Rounded corners
            ) {
                itemList.forEachIndexed { index, item ->
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
                            text = item,
                            color = if (index == selectedIndex) Color.Black else Color(0xFFBDBDBD), // Black color for selected item
                            modifier = Modifier.padding(horizontal = 16.dp) // Padding inside each item
                        )
                    }
                }
            }
        }
    }
}
//@Composable
//fun CalendarContentMgr(
//    currentMonth: YearMonth,
//    onDateSelected: (LocalDate) -> Unit,
//    showMonthNavigation: Boolean = false,
//    onPreviousMonth: (() -> Unit)? = null,
//    onNextMonth: (() -> Unit)? = null,
//    selectedDate: LocalDate? = null,
//    restrictDateSelection: Boolean = false
//) {
//    val daysOfWeek = listOf(
//        DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
//        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY
//    )
//
//    val today = LocalDate.now()
//
//    Column(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        if (showMonthNavigation) {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.Start,
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                IconButton(onClick = { onPreviousMonth?.invoke() }) {
//                    Icon(
//                        imageVector = Icons.Default.KeyboardArrowLeft,
//                        contentDescription = "Previous Month",
//                        tint = LightGrassGreen
//                    )
//                }
//
//                Text(
//                    text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
//                    fontSize = 16.sp,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier.padding(16.dp)
//                )
//
//                IconButton(onClick = { onNextMonth?.invoke() }) {
//                    Icon(
//                        imageVector = Icons.Default.KeyboardArrowRight,
//                        contentDescription = "Next Month",
//                        tint = LightGrassGreen
//                    )
//                }
//            }
//        } else {
//            Text(
//                text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
//                fontSize = 24.sp,
//                fontWeight = FontWeight.Bold,
//                modifier = Modifier.padding(16.dp)
//            )
//        }
//
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceAround
//        ) {
//            daysOfWeek.forEach { day ->
//                Text(
//                    text = day.getDisplayName(TextStyle.NARROW, Locale.getDefault()),
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier.padding(8.dp)
//                )
//            }
//        }
//
//        val firstDayOfMonth = currentMonth.atDay(1)
//        val lastDayOfMonth = currentMonth.atEndOfMonth()
//        val daysInMonth = (1..lastDayOfMonth.dayOfMonth).map { firstDayOfMonth.plusDays((it - 1).toLong()) }
//
//        val previousMonthDays = (1..firstDayOfMonth.dayOfWeek.value % 7).map {
//            firstDayOfMonth.minusDays(it.toLong())
//        }.reversed()
//
//        val totalDays = previousMonthDays.size + daysInMonth.size
//        val remainingDays = 7 - totalDays % 7
//
//        val nextMonthDays = if (remainingDays < 7) (1..remainingDays).map {
//            lastDayOfMonth.plusDays(it.toLong())
//        } else emptyList()
//
//        val daysWithBlanks = mutableListOf<LocalDate?>()
//        daysWithBlanks.addAll(previousMonthDays)
//        daysWithBlanks.addAll(daysInMonth)
//        daysWithBlanks.addAll(nextMonthDays)
//
//        daysWithBlanks.chunked(7).forEach { week ->
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceAround
//            ) {
//                week.forEach { day ->
//                    val isWeekend = day?.dayOfWeek == DayOfWeek.SATURDAY || day?.dayOfWeek == DayOfWeek.SUNDAY
//                    val isWorkFromOffice = day?.dayOfWeek == DayOfWeek.MONDAY || day?.dayOfWeek == DayOfWeek.TUESDAY || day?.dayOfWeek == DayOfWeek.WEDNESDAY
//                    val isWorkFromHome = day?.dayOfWeek == DayOfWeek.THURSDAY || day?.dayOfWeek == DayOfWeek.FRIDAY
//
//                    val textColor = when {
//                        isWeekend -> Color.Gray
//                        isWorkFromHome -> DarkGrassGreen2
//                        isWorkFromOffice -> DarkTeal2
//                        else -> Color.Black
//                    }
//                    val textAlpha = if (day?.month != currentMonth.month) 0.3f else 1f
//                    val isToday = day == today
//
//                    val isDisabled = restrictDateSelection && day?.let {
//                        it.isBefore(today) || it == selectedDate || isWeekend
//                    } ?: false
//
//                    Box(
//                        contentAlignment = Alignment.Center,
//                        modifier = Modifier
//                            .size(35.dp)
//                            .padding(4.dp)
//                            .clickable(enabled = !isDisabled && day != null && day.month == currentMonth.month) {
//                                day?.let { onDateSelected(it) }
//                            }
//                            .border(
//                                width = if (isToday) 2.dp else 0.dp,
//                                color = when {
//                                    isToday && isWeekend -> Color.Gray
//                                    isToday && isWorkFromHome -> DarkGrassGreen2
//                                    isToday && isWorkFromOffice -> DarkTeal2
//                                    else -> Color.Transparent
//                                },
//                                shape = CircleShape
//                            )
//                            .background(
//                                color = if (isToday) Color.Transparent else Color.Transparent,
//                                shape = CircleShape
//                            )
//                    ) {
//                        if (day != null) {
//                            Text(
//                                text = day.dayOfMonth.toString(),
//                                color = textColor,
//                                modifier = Modifier.alpha(if (isDisabled) 0.3f else textAlpha)
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//}

//@Composable
//fun CalendarView(context: Context, userId: String?) {
//    userId?.let {
//        var currentMonth by remember { mutableStateOf(YearMonth.now()) }
//
//        CalendarContent(
//            context = context,
//            userId = it,
//            onDateSelected = {},
//            showMonthNavigation = true,
//            onPreviousMonth = { currentMonth = currentMonth.minusMonths(1) },
//            onNextMonth = { currentMonth = currentMonth.plusMonths(1) }
//        )
//    }
//}
//@Composable
//fun LegendItemMgr(color: Color, label: String) {
//    Row(
////        verticalAlignment = Alignment.CenterVertically,
////        horizontalArrangement = Arrangement.Start,
////        modifier = Modifier.padding(end = 12.dp)
//        verticalAlignment = Alignment.CenterVertically,
//        modifier = Modifier.padding(horizontal = 8.dp)
//    ) {
//        Box(
//            modifier = Modifier
//                .size(16.dp)
//                .clip(RoundedCornerShape(10.dp))
//                .background(color)
//        )
//        Text(
//            text = label,
//            color = Color.Black,
//            fontSize = 16.sp,
//            modifier = Modifier.padding(start = 8.dp)
//        )
//    }
//}
