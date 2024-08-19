package com.example.project

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
// Fetch requests from the backend API
private fun fetchRequests(token: String, context: Context, onResult: (List<Request>) -> Unit) {
    val apiService = RetrofitClient.apiService
    Log.d("Token in fetchRequests:", "$token")

    apiService.getRequests("Bearer $token")
        .enqueue(object : Callback<List<Request>> {
            override fun onResponse(call: Call<List<Request>>, response: Response<List<Request>>) {
                if (response.isSuccessful) {
                    val requests = response.body() ?: emptyList()
                    onResult(requests)
                    Log.d("Fetched Requests:", "$requests")
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error occurred"
                    Log.e("Requests", "Error fetching requests: $errorMessage")
                    Toast.makeText(context, "Failed to fetch requests: $errorMessage", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<Request>>, t: Throwable) {
                Log.e("Requests", "Failure fetching requests: ${t.localizedMessage}", t)
                Toast.makeText(context, "Failed to fetch requests: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })
}

@Composable
fun ManagerRequests(context: Context) {
    var requests by remember { mutableStateOf(emptyList<Request>()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Retrieve the token from SharedPreferences
    val token = PreferencesManager.getTokenFromPreferences(context)

    // Fetch requests when the composable is first launched
    LaunchedEffect(Unit) {
        token?.let {
            fetchRequests(it, context) { fetchedRequests ->
                requests = fetchedRequests
                isLoading = false
            }
        } ?: run {
            isLoading = false
            errorMessage = "No token found"
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
            .fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        if (isLoading) {
            // Show a loading spinner or message
            Text(
                text = "Loading...",
                fontSize = 18.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (errorMessage != null) {
            // Show the error message
            Text(
                text = errorMessage!!,
                fontSize = 18.sp,
                color = Color.Red,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(35.dp))
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()) // Enable vertical scrolling
            ) {
                Text(
                    text = "Manager Requests",
                    fontSize = 35.sp,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Divider(
                    color = Color.Gray.copy(alpha = 0.5f),
                    thickness = 1.dp,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .alpha(0.5f)
                )
                requests.forEachIndexed { index, request ->
                    ManagerRequestItem(
                        request = request,
                        onApprove = { updatedRequest -> /* Handle approve action */ },
                        onDeny = { updatedRequest -> /* Handle deny action */ }
                    )
                    if (index < requests.size - 1) {
                        Divider(
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .alpha(0.5f),
                            thickness = 1.dp,
                            color = Color.Gray.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}
enum class Action {
    APPROVE,
    DENY
}

@Composable
fun ManagerRequestItem(
    request: Request,
    onApprove: (Request) -> Unit,
    onDeny: (Request) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var currentAction by remember { mutableStateOf<Action?>(null) }
    var status by remember { mutableStateOf(request.status ?: RequestStatus.PENDING) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Are you sure you want to ${currentAction?.name?.lowercase()} this request?") },
            confirmButton = {
                Button(
                    onClick = {
                        when (currentAction) {
                            Action.APPROVE -> {
                                onApprove(request.copy(status = RequestStatus.APPROVED))
                                status = RequestStatus.APPROVED
                            }
                            Action.DENY -> {
                                onDeny(request.copy(status = RequestStatus.DENIED))
                                status = RequestStatus.DENIED
                            }
                            else -> {}
                        }
                        showDialog = false
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text("No")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = request.userName ?: "No UserName available",
            fontSize = 20.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        Text(
            text = request.timeAgo ?: "Unknown time",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier
                .padding(bottom = 4.dp)
                .alpha(0.5f)
        )
        Text(
            text = request.description ?: "No description available",
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (status == RequestStatus.PENDING) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(40.dp)
                            .clickable {
                                currentAction = Action.DENY
                                showDialog = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ghalat), // Replace with your deny icon resource ID
                            contentDescription = "Deny",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(40.dp)
                            .clickable {
                                currentAction = Action.APPROVE
                                showDialog = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.sa7), // Replace with your approve icon resource ID
                            contentDescription = "Approve",
                            modifier = Modifier.size(45.dp)
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .background(
                            color = if (status == RequestStatus.APPROVED) Color(0xFF00cc99) else Color(0xFFeb5757),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (status == RequestStatus.APPROVED) "Approved" else "Denied",
                        fontSize = 16.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}