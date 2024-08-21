package com.example.project

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.project.components.CalendarContent
import com.example.project.components.CalendarView
import com.example.project.components.LegendItem
import com.example.project.components.parseUserIdFromToken
import com.example.project.ui.theme.DarkGrassGreen2
import com.example.project.ui.theme.DarkTeal2


import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



@SuppressLint("SuspiciousIndentation")
@Composable
fun HomeScreen(context: Context) {
    // State variables to hold user data
    var userName by remember { mutableStateOf("User") }
    var todayStatus by remember { mutableStateOf("Loading...") }
    val token = PreferencesManager.getTokenFromPreferences(context)

    // Extract the userId from the token
    val userId = token?.let { parseUserIdFromToken(it) }

    // Fetch user data on load
    LaunchedEffect(userId) {
        println("Extracted User ID: $userId")
        userId?.let {
            val call = RetrofitClient.apiService.getUserInfo(it)
            call.enqueue(object : Callback<UserInfoResponse> {
                override fun onResponse(
                    call: Call<UserInfoResponse>,
                    response: Response<UserInfoResponse>
                ) {
                    println("Raw response: ${response.raw()}")
                    if (response.isSuccessful) {
                        val userInfo = response.body()
                        println("Response body: $userInfo")
                        if (userInfo != null) {
                            // This runs on the main thread
                            userName = userInfo.name ?: "Unknown"
                            todayStatus = when (userInfo.todaySchedule) {
                                "Home" -> "Today, you are working from Home!"
                                "Office" -> "Today, you are working from Office!"
                                else -> "It's Weekend Time!"
                            }
                        } else {
                            todayStatus = "Failed to load status"
                        }
                    } else {
                        todayStatus = "Failed to load status"
                    }
                }

                override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
                    // Print the error message
                    println("Request failed: ${t.message}")
                    todayStatus = "Error: ${t.message}"
                }
            })
        } ?: run {
            todayStatus = "Invalid token or user ID"
        }
    }

    // Current date formatting
    val currentDate = remember {
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("d EEE\nMMM yyyy")
        today.format(formatter)
    }

    var selectedDay by remember { mutableStateOf<LocalDate?>(null) }
    var changeToDay by remember { mutableStateOf<LocalDate?>(null) }

    // Annotated string for current date
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

                    // modifier = Modifier,

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
                        text = userName,
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
            TodayStatusBox(message = todayStatus)
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

                    if (userId != null) {
                        CalendarView(context, userId)
                    }

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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(9.dp)
            ) {
                Column {
                    Text(
                        text = "Schedule change requests",
                        fontSize = 18.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    if (userId != null) {
                        DatePickerWithLabel(
                            context = context,
                            userId = userId,
                            label = "Select day",
                            selectedDate = selectedDay,
                            onDateSelected = { selectedDay = it }
                        )
                    }


                    Spacer(modifier = Modifier.height(8.dp))

                    if (userId != null) {
                        DatePickerWithLabel(
                            context = context,
                            userId = userId,
                            label = "Change to",
                            selectedDate = changeToDay,
                            onDateSelected = { changeToDay = it }
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    val isSubmitEnabled = selectedDay != null && changeToDay != null

                    var reason by remember { mutableStateOf("") }

                    TextField(
                        value = reason,
                        onValueChange = { reason = it },
                        placeholder = { Text("Reason for change...", color = Color(0xFFBDBDBD)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 0.dp) // Add horizontal padding
                            .height(54.dp) // Smaller height
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFFBDBDBD), RoundedCornerShape(8.dp)), // Align border radius with background radius
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            disabledContainerColor = Color.White,
                            cursorColor = Color.Black, // Optionally set the cursor color to black
                            focusedIndicatorColor = Color.Transparent, // Remove the underline when focused
                            unfocusedIndicatorColor = Color.Transparent, // Remove the underline when not focused
                            disabledIndicatorColor = Color.Transparent, // Remove the underline when disabled
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (isSubmitEnabled) {
                                // Create request object with reason
                                val request = SubmitRequest(
                                    selectedDay.toString(),
                                    changeToDay.toString(),
                                    reason // Add reason here
                                )

                                if (token != null) {
                                    // Make the API call using Retrofit
                                    RetrofitClient.apiService.submitRequest("Bearer $token", request).enqueue(object : Callback<SubmitRequestResponse> {
                                        override fun onResponse(call: Call<SubmitRequestResponse>, response: Response<SubmitRequestResponse>) {
                                            if (response.isSuccessful) {
                                                val submitResponse = response.body()
                                                if (submitResponse != null) {
                                                    // Handle successful submission
                                                    Toast.makeText(context, submitResponse.message, Toast.LENGTH_LONG).show()
                                                    Log.d("SubmitRequest", "Request submitted: ${submitResponse.requestId}")

                                                    // Clear the input fields
                                                    selectedDay = null
                                                    changeToDay = null
                                                    reason = ""
                                                } else {
                                                    // Handle null response body
                                                    Toast.makeText(context, "Failed to submit request", Toast.LENGTH_LONG).show()
                                                    Log.e("SubmitRequest", "Submit response was null")
                                                }
                                            } else {
                                                // Handle failed response
                                                val errorMessage = response.errorBody()?.string() ?: "Failed to submit request"
                                                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                                                Log.e("SubmitRequest", "Submit failed: ${response.message()}")
                                            }
                                        }

                                        override fun onFailure(call: Call<SubmitRequestResponse>, t: Throwable) {
                                            // Handle API call failure
                                            Toast.makeText(context, "Failed to submit request: ${t.message}", Toast.LENGTH_LONG).show()
                                            Log.e("SubmitRequest", "API call failed: ${t.message}")
                                        }
                                    })
                                } else {
                                    // Handle missing token
                                    Toast.makeText(context, "Token is missing from SharedPreferences", Toast.LENGTH_LONG).show()
                                    Log.e("SubmitRequest", "Token is missing from SharedPreferences")
                                }
                            }
                        },
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
fun TodayStatusBox(message: String) {
    val backgroundColor = when {
        message.contains("Home") -> DarkGrassGreen2
        message.contains("Office") -> DarkTeal2
        else -> Color.Gray

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
fun CalendarViewForDialog(
    context: Context,
    userId: String,
    currentMonth: YearMonth,
    onDateSelected: (LocalDate) -> Unit,
    selectedDate: LocalDate?
) {
    val nextMonth = YearMonth.now().plusMonths(1)
    var displayedMonth by remember { mutableStateOf(currentMonth) }

    CalendarContent(
        context = context,
        userId = userId,
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
fun DatePickerWithLabel(
    context: Context,
    userId: String,
    label: String,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit
) {
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
                        context = context,
                        userId = userId,
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