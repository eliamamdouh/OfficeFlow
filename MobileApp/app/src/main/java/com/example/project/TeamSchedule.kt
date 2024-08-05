package com.example.project
//FIX PACKAGE


import android.annotation.SuppressLint
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
import kotlinx.coroutines.delay
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale


@Composable
fun ScheduleScreen() {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedTeam by remember { mutableIntStateOf(-1) } // No team selected initially
    var showPopup by remember { mutableStateOf(false) }
    val teamList = listOf("Member Team 1", "Member Team 2", "Member Team 3", "Member Team 4")

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
                                itemList = teamList,
                                selectedIndex = selectedTeam,
                                onItemClick = { selectedTeam = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp)
                            )

                            if (selectedTeam != -1) {
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

                                CalendarContentMgr(
                                    currentMonth = currentMonth,
                                    onDateSelected = {},
                                    showMonthNavigation = true,
                                    onPreviousMonth = { currentMonth = currentMonth.minusMonths(1) },
                                    onNextMonth = { currentMonth = currentMonth.plusMonths(1) }
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
                                    LegendItemMgr(color = DarkTeal2, label = "From Office")
                                    LegendItemMgr(color = DarkGrassGreen2, label = "From Home")
                                }

                                // Button below the calendar and legend items
                                Button(
                                    onClick = {
                                        showPopup = true
                                        // Handle saving logic here
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = DarkGrassGreen2,
                                        disabledContainerColor = Color(0xFFC7C7C7) // Ensure this is set for disabled state
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 10.dp)
                                        .padding(horizontal = 15.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                ) {
                                    Text(
                                        text = "Save",
                                        color = Color.White,
                                        fontSize = 18.sp
                                    )
                                }

                                // Show popup if needed
                                if (showPopup) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()// Center the popup horizontally at the top
                                    ) {
                                        Popup(
                                            onDismissRequest = { showPopup = false },
                                            properties = PopupProperties(focusable = true)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .background(Color(0xFF2C8431), RoundedCornerShape(8.dp))
                                                    .padding(14.dp)
                                            ) {
                                                Text(
                                                    text = "Request has been saved successfully!!",
                                                    color = Color.White, // Ensure text is visible
                                                    fontSize = 16.sp
                                                )
                                            }
                                        }
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
                tint = Color(0xFF86BC24), // Arrow color
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
//fun TodayStatusBox(day: LocalDate?) {
//    val message = when (day?.dayOfWeek) {
//        DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY -> "Today, you are working from Office!"
//        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY -> "Today, you are working from Home!"
//        DayOfWeek.SATURDAY, DayOfWeek.SUNDAY -> "It's Weekend Time!"
//        else -> ""
//    }
//
//    val backgroundColor = when (day?.dayOfWeek) {
//        DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY -> DarkTeal2
//        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY -> DarkGrassGreen2
//        DayOfWeek.SATURDAY, DayOfWeek.SUNDAY -> Color.Gray
//        else -> Color.Transparent
//    }
//
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 8.dp)
//            .background(backgroundColor, RoundedCornerShape(8.dp))
//            .padding(8.dp)
//    ) {
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier.fillMaxHeight()
//        ) {
//            Text(
//                text = message,
//                fontSize = 14.sp,
//                color = Color.White,
//                modifier = Modifier.padding(start = 8.dp)
//            )
//        }
//    }
//}


@Composable
fun CalendarContentMgr(
    currentMonth: YearMonth,
    onDateSelected: (LocalDate) -> Unit,
    showMonthNavigation: Boolean = false,
    onPreviousMonth: (() -> Unit)? = null,
    onNextMonth: (() -> Unit)? = null,
    selectedDate: LocalDate? = null,
    restrictDateSelection: Boolean = false
) {
    val daysOfWeek = listOf(
        DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY
    )

    val today = LocalDate.now()

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
                IconButton(onClick = { onPreviousMonth?.invoke() }) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Previous Month",
                        tint = LightGrassGreen
                    )
                }

                Text(
                    text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )

                IconButton(onClick = { onNextMonth?.invoke() }) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Next Month",
                        tint = LightGrassGreen
                    )
                }
            }
        } else {
            Text(
                text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day.getDisplayName(TextStyle.NARROW, Locale.getDefault()),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        val firstDayOfMonth = currentMonth.atDay(1)
        val lastDayOfMonth = currentMonth.atEndOfMonth()
        val daysInMonth = (1..lastDayOfMonth.dayOfMonth).map { firstDayOfMonth.plusDays((it - 1).toLong()) }

        val previousMonthDays = (1..firstDayOfMonth.dayOfWeek.value % 7).map {
            firstDayOfMonth.minusDays(it.toLong())
        }.reversed()

        val totalDays = previousMonthDays.size + daysInMonth.size
        val remainingDays = 7 - totalDays % 7

        val nextMonthDays = if (remainingDays < 7) (1..remainingDays).map {
            lastDayOfMonth.plusDays(it.toLong())
        } else emptyList()

        val daysWithBlanks = mutableListOf<LocalDate?>()
        daysWithBlanks.addAll(previousMonthDays)
        daysWithBlanks.addAll(daysInMonth)
        daysWithBlanks.addAll(nextMonthDays)

        daysWithBlanks.chunked(7).forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                week.forEach { day ->
                    val isWeekend = day?.dayOfWeek == DayOfWeek.SATURDAY || day?.dayOfWeek == DayOfWeek.SUNDAY
                    val isWorkFromOffice = day?.dayOfWeek == DayOfWeek.MONDAY || day?.dayOfWeek == DayOfWeek.TUESDAY || day?.dayOfWeek == DayOfWeek.WEDNESDAY
                    val isWorkFromHome = day?.dayOfWeek == DayOfWeek.THURSDAY || day?.dayOfWeek == DayOfWeek.FRIDAY

                    val textColor = when {
                        isWeekend -> Color.Gray
                        isWorkFromHome -> DarkGrassGreen2
                        isWorkFromOffice -> DarkTeal2
                        else -> Color.Black
                    }
                    val textAlpha = if (day?.month != currentMonth.month) 0.3f else 1f
                    val isToday = day == today

                    val isDisabled = restrictDateSelection && day?.let {
                        it.isBefore(today) || it == selectedDate || isWeekend
                    } ?: false

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(35.dp)
                            .padding(4.dp)
                            .clickable(enabled = !isDisabled && day != null && day.month == currentMonth.month) {
                                day?.let { onDateSelected(it) }
                            }
                            .border(
                                width = if (isToday) 2.dp else 0.dp,
                                color = when {
                                    isToday && isWeekend -> Color.Gray
                                    isToday && isWorkFromHome -> DarkGrassGreen2
                                    isToday && isWorkFromOffice -> DarkTeal2
                                    else -> Color.Transparent
                                },
                                shape = CircleShape
                            )
                            .background(
                                color = if (isToday) Color.Transparent else Color.Transparent,
                                shape = CircleShape
                            )
                    ) {
                        if (day != null) {
                            Text(
                                text = day.dayOfMonth.toString(),
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

//@Composable
//fun CalendarView() {
//    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
//
//    CalendarContentMgr(
//        currentMonth = currentMonth,
//        onDateSelected = {},
//        showMonthNavigation = true,
//        onPreviousMonth = { currentMonth = currentMonth.minusMonths(1) },
//        onNextMonth = { currentMonth = currentMonth.plusMonths(1) }
//    )
//}
//
//@Composable
//fun CalendarViewForDialog(currentMonth: YearMonth, onDateSelected: (LocalDate) -> Unit, selectedDate: LocalDate?) {
//    CalendarContentMgr(
//        currentMonth = currentMonth,
//        onDateSelected = onDateSelected,
//        showMonthNavigation = false,
//        selectedDate = selectedDate,
//        restrictDateSelection = true
//    )
//}


@Composable
fun LegendItemMgr(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.padding(end = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(color)
        )
        Text(
            text = label,
            color = Color.Black,
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}
