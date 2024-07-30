package com.example.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.ArrowForward
//import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.project.ui.theme.ProjectTheme
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProjectTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "login") {
                    composable("login") { LoginScreen(navController) }
                    composable("home") { HomeScreen() }
                }
            }
        }
    }
}
@Composable
fun Logo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(id = R.drawable.logo4),
        contentDescription = "",
        modifier = modifier
    )
}

@Composable
fun BackgroundImage() {
    Image(
        painter = painterResource(id = R.drawable.splash1),
        contentDescription = "",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun LoginScreen(navController: NavHostController) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background image
        BackgroundImage()

        // Foreground content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Logo
                    Logo(modifier = Modifier.size(250.dp))

                    Text(
                        text = stringResource(id = R.string.login),
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 20.dp)
                    )
                }
            }

            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text(text = "Enter your username") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(30.dp))
                    .padding(vertical = 10.dp),
                singleLine = true
            )

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(text = "Enter your password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(30.dp))
                    .padding(vertical = 8.dp),
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (passwordVisible) "Hide password" else "Show password"

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = description)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

//            val isLoginEnabled = username.isNotEmpty() && password.isNotEmpty()
            Button(
                onClick = { navController.navigate("home") },
//                enabled = isLoginEnabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor =  Color.Green// if(isLoginEnabled) Color.Green else Color.Gray,
//                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 18.dp),
            ) {
                Text(text = "Log In", color = Color.White)
            }
        }
    }
}



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
            addStyle(SpanStyle(color = Color.Gray), 0, dayAndWeek.length)

            append("\n")

            val monthYearStartIndex = dayAndWeek.length + 1 // +1 for the newline character
            append(monthAndYear)
            addStyle(SpanStyle(color = Color.Black), monthYearStartIndex, monthYearStartIndex + monthAndYear.length)
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
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    Text(
                        text = "Mohammed",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Text(
                    text = annotatedString, // currentDate,
                    fontSize = 18.sp,
//                    color = Color.Gray,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        item {
            TodayStatusBox(day = LocalDate.now())
        }

        item {
            Text(
                text = "Working from home/office schedule",
                fontSize = 18.sp,
                color = Color.Black,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.alpha(0.3f))
        }

        item {
            CalendarView()
        }

        item {
            Divider(color = Color.Gray, thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
                    .alpha(0.3f))
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            )
            {
                LegendItem(color = Color(0xFF008080), label = "From Office")
                LegendItem(color = Color(0xFF006400), label = "From Home")
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Text(
                text = "Schedule change requests",
                fontSize = 18.sp,
                color = Color.Black,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        item {
            DatePickerWithLabel(
                label = "Select day",
                selectedDate = selectedDay,
                onDateSelected = { selectedDay = it }
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            DatePickerWithLabel(
                label = "Change to",
                selectedDate = changeToDay,
                onDateSelected = { changeToDay = it }
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            val isSubmitEnabled = selectedDay != null && changeToDay != null

            Button(
                onClick = { /* Later */ },
                enabled = isSubmitEnabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSubmitEnabled) Color(0xFF006400) else Color.Gray
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Submit", color = Color.White)
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
        DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY -> Color(0xFF008080)
        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY -> Color(0xFF006400)
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
        Text(
            text = message,
            fontSize = 18.sp,
            color = Color.White,
            modifier = Modifier.padding(top = 10.dp)
        )
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
                        tint = Color(0xFF006400)
                    )
                }

                Text(
                    text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )

                IconButton(onClick = { onNextMonth?.invoke() }) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Next Month",
                        tint = Color(0xFF006400)
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
                        isWorkFromHome -> Color(0xFF006400)
                        isWorkFromOffice -> Color(0xFF008080)
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
                            .background(
                                color = if (isToday) Color.LightGray else Color.Transparent,
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
    CalendarContent(
        currentMonth = currentMonth,
        onDateSelected = onDateSelected,
        showMonthNavigation = false,
        selectedDate = selectedDate,
        restrictDateSelection = true
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
                text = selectedDate?.format(DateTimeFormatter.ofPattern("dd MMM yyyy")) ?: label,
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