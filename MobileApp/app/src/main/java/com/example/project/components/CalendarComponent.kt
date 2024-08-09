package com.example.project.components

//import androidx.compose.foundation.lazy.items


import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import com.example.project.RetrofitClient
import com.example.project.ScheduleDay
import com.example.project.ScheduleResponse
import com.example.project.ui.theme.DarkGrassGreen2
import com.example.project.ui.theme.DarkTeal2
import com.example.project.ui.theme.LightGrassGreen
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


@Composable
fun CalendarContent(
    context: Context,
    userId: String,
    onDateSelected: (LocalDate) -> Unit = {},
    showMonthNavigation: Boolean = false,
    onPreviousMonth: (() -> Unit)? = null,
    onNextMonth: (() -> Unit)? = null,
    selectedDate: LocalDate? = null,
    restrictDateSelection: Boolean = false,
    isDialog: Boolean = false
) {
    var schedule by remember { mutableStateOf<Map<String, List<ScheduleDay>>?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fetch schedule data
    LaunchedEffect(userId) {
        val call = RetrofitClient.apiService.viewSchedule(userId)
        call.enqueue(object : Callback<ScheduleResponse> {
            override fun onResponse(call: Call<ScheduleResponse>, response: Response<ScheduleResponse>) {
                if (response.isSuccessful) {
                    schedule = response.body()?.schedule
                    println("Schedule fetched successfully: $schedule")
                } else {
                    errorMessage = "Error fetching schedule: ${response.errorBody()?.string()}"
                    println(errorMessage)
                }
            }

            override fun onFailure(call: Call<ScheduleResponse>, t: Throwable) {
                errorMessage = "Failed to fetch schedule: ${t.message}"
                println(errorMessage)
            }
        })
    }

    val currentMonth = YearMonth.now()
    val daysOfWeek = listOf(
        DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY
    )

    val today = LocalDate.now()
    val nextMonth = YearMonth.now().plusMonths(1)
    val isCurrentMonth = currentMonth == YearMonth.now()
    val isNextMonth = currentMonth == nextMonth

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (showMonthNavigation) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isDialog || (isDialog && !isCurrentMonth)) {
                    IconButton(onClick = { onPreviousMonth?.invoke() }) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Previous Month",
                            tint = LightGrassGreen
                        )
                    }
                }

                Text(
                    text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )

                if (!isDialog || (isDialog && !isNextMonth)) {
                    IconButton(onClick = { onNextMonth?.invoke() }) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Next Month",
                            tint = LightGrassGreen
                        )
                    }
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            daysOfWeek.forEach { dayOfWeek ->
                Text(
                    text = dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.getDefault()),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        val firstDayOfMonth = currentMonth.atDay(1)
        val lastDayOfMonth = currentMonth.atEndOfMonth()
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value
        val offset = if (firstDayOfWeek == 7) 6 else firstDayOfWeek % 7  // Adjust for Sunday start
        val totalDaysInMonth = lastDayOfMonth.dayOfMonth

        // Build the calendar grid with the schedule data
        val weeksInMonth = (totalDaysInMonth + offset - 1) / 7 + 1
        for (week in 0 until weeksInMonth) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (day in 1..7) {
                    val dayOfMonth = week * 7 + day - offset
                    val date = if (dayOfMonth in 1..totalDaysInMonth) currentMonth.atDay(dayOfMonth) else null

                    val isWeekend = date?.dayOfWeek == DayOfWeek.SATURDAY || date?.dayOfWeek == DayOfWeek.SUNDAY
                    val isWorkFromHome = schedule?.values?.flatten()?.any { it.day == date.toString() && it.location == "Home" } == true
                    val isWorkFromOffice = schedule?.values?.flatten()?.any { it.day == date.toString() && it.location == "Office" } == true

                    // Debug print to check schedule data
                    if (date != null) {
                        println("Date: $date, Home: $isWorkFromHome, Office: $isWorkFromOffice, Schedule: ${schedule?.get(date.toString())}")
                    }

                    val textColor = when {
                        isWorkFromHome -> DarkGrassGreen2
                        isWorkFromOffice -> DarkTeal2
                        isWeekend -> Color.Gray
                        else -> Color.Black
                    }

                    val textAlpha = if (date?.month != currentMonth.month) 0.3f else 1f
                    val isToday = date == today

                    val isDisabled = restrictDateSelection && date?.let {
                        it.isBefore(today) || it == selectedDate || isWeekend
                    } ?: false

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                color = when {
                                    date == null -> Color.Transparent
                                    date == selectedDate -> LightGrassGreen
                                    else -> Color.Transparent
                                }
                            )
                            .border(
                                width = if (isToday) 2.dp else 0.dp,
                                color = when {
                                    isToday && isWorkFromOffice -> DarkTeal2
                                    isToday && isWorkFromHome -> DarkGrassGreen2
                                    isToday && isWeekend -> Color.Gray
                                    else -> Color.Transparent
                                },
                                shape = CircleShape
                            )
                            .clickable(enabled = !isDisabled && date != null && date.month == currentMonth.month) {
                                date?.let { onDateSelected(it) }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (date != null) {
                            Text(
                                text = date.dayOfMonth.toString(),
                                color = textColor,
                                modifier = Modifier.alpha(if (isDisabled) 0.3f else textAlpha)
                            )
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun CalendarView(context: Context, userId: String?) {
    userId?.let {
        var currentMonth by remember { mutableStateOf(YearMonth.now()) }

        CalendarContent(
            context = context,
            userId = it,
            onDateSelected = {},
            showMonthNavigation = true,
            onPreviousMonth = { currentMonth = currentMonth.minusMonths(1) },
            onNextMonth = { currentMonth = currentMonth.plusMonths(1) }
        )
    }
}
