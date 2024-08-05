package com.example.project

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
//import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily

val LightGrassGreen = Color(0xFF86BC24)
//val DarkTeal = Color(0xFF008080)
val DarkTeal2 = Color(0xFF036B80)
//val DarkGrassGreen = Color(0xFF006400)
val DarkGrassGreen2 = Color(0xFF2C8431)




@Composable
fun HomeScreen() {
    val currentDate = remember {
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("d EEE\nMMM yyyy")
        today.format(formatter)
    }

    val annotatedString = remember {
        buildAnnotatedString {
            val parts = currentDate.split("\n")
            val dayAndWeek = parts[0]
            val monthAndYear = parts[1]

            append(dayAndWeek)
            addStyle(SpanStyle(color = Color.Gray, fontSize = 14.sp), 0, dayAndWeek.length)

            append("\n")

            val monthYearStartIndex = dayAndWeek.length + 1 // +1 for the newline character
            append(monthAndYear)
            addStyle(SpanStyle(color = Color.Black, fontSize = 14.sp), monthYearStartIndex, monthYearStartIndex + monthAndYear.length)
        }
    }

    var selectedDay by remember { mutableStateOf<LocalDate?>(null) }
    var changeToDay by remember { mutableStateOf<LocalDate?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier,
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Welcome Back",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Text(
                        text = "Mohammed",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Text(
                    text = annotatedString,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        item {
            TodayStatusBox(day = LocalDate.now())
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "Working from home/office schedule",
                        fontFamily = FontFamily.SansSerif,
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.alpha(0.3f))

                    CalendarView()

                    Divider(
                        color = Color.Gray,
                        thickness = 1.dp,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
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
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "Schedule change requests",
                        fontSize = 18.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    DatePickerWithLabel(
                        label = "Select day",
                        selectedDate = selectedDay,
                        onDateSelected = { selectedDay = it }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    DatePickerWithLabel(
                        label = "Change to",
                        selectedDate = changeToDay,
                        onDateSelected = { changeToDay = it }
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    val isSubmitEnabled = selectedDay != null && changeToDay != null

                    Button(
                        onClick = { /* Later */ },
                        enabled = isSubmitEnabled,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSubmitEnabled) DarkGrassGreen2 else Color.Gray
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Submit", fontSize = 16.sp, color = Color.White)
                    }
                }
            }
        }
    }
}



@Composable
fun TodayStatusBox(day: LocalDate?) {
    val message = when (day?.dayOfWeek) {
        DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY -> "Today, you are working from Office!"
        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY -> "Today, you are working from Home!"
        DayOfWeek.SATURDAY, DayOfWeek.SUNDAY -> "It's Weekend Time!"
        else -> ""
    }

    val backgroundColor = when (day?.dayOfWeek) {
        DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY -> DarkTeal2
        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY -> DarkGrassGreen2
        DayOfWeek.SATURDAY, DayOfWeek.SUNDAY -> Color.Gray
        else -> Color.Transparent
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxHeight()
        ) {
            Text(
                text = message,
                fontSize = 14.sp,
                color = Color.White,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}


@Composable
fun CalendarContent(
    currentMonth: YearMonth,
    onDateSelected: (LocalDate) -> Unit,
    showMonthNavigation: Boolean = false,
    onPreviousMonth: (() -> Unit)? = null,
    onNextMonth: (() -> Unit)? = null,
    selectedDate: LocalDate? = null,
    restrictDateSelection: Boolean = false,
    isDialog: Boolean = false
) {
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
@Composable
fun CalendarView() {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    CalendarContent(
        currentMonth = currentMonth,
        onDateSelected = {},
        showMonthNavigation = true,
        onPreviousMonth = { currentMonth = currentMonth.minusMonths(1) },
        onNextMonth = { currentMonth = currentMonth.plusMonths(1) }
    )
}

@Composable
fun CalendarViewForDialog(currentMonth: YearMonth, onDateSelected: (LocalDate) -> Unit, selectedDate: LocalDate?) {
    val nextMonth = YearMonth.now().plusMonths(1)
    var displayedMonth by remember { mutableStateOf(currentMonth) }

    CalendarContent(
        currentMonth = displayedMonth,
        onDateSelected = onDateSelected,
        showMonthNavigation = true,
        onPreviousMonth = {
            if (displayedMonth.isAfter(YearMonth.now())) {
                displayedMonth = displayedMonth.minusMonths(1)
            }
        },
        onNextMonth = {
            if (displayedMonth.isBefore(nextMonth)) {
                displayedMonth = displayedMonth.plusMonths(1)
            }
        },
        selectedDate = selectedDate,
        restrictDateSelection = true,
        isDialog = true
    )
}
@Composable
fun DatePickerWithLabel(label: String, selectedDate: LocalDate?, onDateSelected: (LocalDate) -> Unit) {
    var isDialogOpen by remember { mutableStateOf(false) }
    var tempDate by remember { mutableStateOf(LocalDate.now()) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
            .clickable { isDialogOpen = true }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = selectedDate?.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
                    ?: label,
                color = Color.Gray
            )
            Image(
                painter = painterResource(id = R.drawable.calendar),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
    }

    if (isDialogOpen) {
        Dialog(onDismissRequest = { isDialogOpen = false }) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Select Date",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    CalendarViewForDialog(
                        currentMonth = YearMonth.from(tempDate),
                        onDateSelected = {
                            tempDate = it
                            onDateSelected(it)
                            isDialogOpen = false
                        },
                        selectedDate = selectedDate
                    )
                }
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(color, shape = CircleShape)
        )
        Text(
            text = label,
            fontSize = 16.sp,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

