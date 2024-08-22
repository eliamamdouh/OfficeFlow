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
            Text(
                text = "Loading...",
                fontSize = 18.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (errorMessage != null) {
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
                    text = "Requests",
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
                        token = token!!,  // Pass the token to the composable
                        context = context,
                        onStatusChanged = { updatedRequest ->
                            requests = requests.map {
                                if (it.id == updatedRequest.id) updatedRequest else it
                            }
                        }
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

@Composable
fun ManagerRequestItem(
    request: Request,
    token: String,
    context: Context,
    onStatusChanged: (Request) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var currentAction by remember { mutableStateOf<Action?>(null) }
    var status by remember { mutableStateOf(request.status ?: RequestStatus.PENDING) }

    if (showDialog) {
        ConfirmationDialog(
            action = currentAction,
            onConfirm = { newStatus ->
                val updatedRequest = request.copy(status = newStatus)

                when (newStatus) {
                    RequestStatus.APPROVED -> {
                        acceptRequest(token, request.id, context) { success ->
                            if (success) {
                                onStatusChanged(updatedRequest)
                                status = newStatus
                            }
                        }
                    }
                    RequestStatus.DENIED -> {
                        rejectRequest(token, request.id, context) { success ->
                            if (success) {
                                onStatusChanged(updatedRequest)
                                status = newStatus
                            }
                        }
                    }
                    else -> {}
                }

                showDialog = false
            },
            onDismiss = { showDialog = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        RequestHeader(request = request)
        Text(
            text = request.description ?: "No description available",
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        ActionButtons(
            status = status,
            onApproveClick = {
                currentAction = Action.APPROVE
                showDialog = true
            },
            onDenyClick = {
                currentAction = Action.DENY
                showDialog = true
            }
        )
    }
}

private fun acceptRequest(token: String, requestId: String, context: Context, onResult: (Boolean) -> Unit) {
    val apiService = RetrofitClient.apiService
    val requestIdBody = RequestId(requestId)

    apiService.acceptRequest("Bearer $token", requestIdBody)
        .enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Request accepted", Toast.LENGTH_SHORT).show()
                    onResult(true)
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    Log.e("Request", "Error accepting request: $errorMessage")
                    Toast.makeText(context, "Failed to accept request: $errorMessage", Toast.LENGTH_LONG).show()
                    onResult(false)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("Request", "Failure accepting request: ${t.localizedMessage}", t)
                Toast.makeText(context, "Failed to accept request: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
                onResult(false)
            }
        })
}

private fun rejectRequest(token: String, requestId: String, context: Context, onResult: (Boolean) -> Unit) {
    val apiService = RetrofitClient.apiService
    val requestIdBody = RequestId(requestId)

    apiService.rejectRequest("Bearer $token", requestIdBody)
        .enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Request rejected", Toast.LENGTH_SHORT).show()
                    onResult(true)
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    Log.e("Request", "Error rejecting request: $errorMessage")
                    Toast.makeText(context, "Failed to reject request: $errorMessage", Toast.LENGTH_LONG).show()
                    onResult(false)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("Request", "Failure rejecting request: ${t.localizedMessage}", t)
                Toast.makeText(context, "Failed to reject request: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
                onResult(false)
            }
        })
}
enum class Action {
    APPROVE,
    DENY
}

@Composable
fun RequestHeader(request: Request) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = request.userName ?: "No UserName available",
            fontSize = 20.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = request.timeAgo ?: "Unknown time",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier
                .padding(bottom = 8.dp)
                .alpha(0.5f)
        )
    }
}

@Composable
fun ActionButtons(
    status: RequestStatus,
    onApproveClick: () -> Unit,
    onDenyClick: () -> Unit
) {
    if (status == RequestStatus.PENDING) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                iconId = R.drawable.ghalat,
                contentDescription = "Deny",
                onClick = onDenyClick
            )
            Spacer(modifier = Modifier.width(16.dp))
            IconButton(
                iconId = R.drawable.sa7,
                contentDescription = "Approve",
                onClick = onApproveClick
            )
        }
    } else {
        StatusIndicator(status = status)
    }
}

@Composable
fun IconButton(
    iconId: Int,
    contentDescription: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .size(40.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = iconId),
            contentDescription = contentDescription,
            modifier = Modifier.size(40.dp)
        )
    }
}

@Composable
fun StatusIndicator(status: RequestStatus) {
    val backgroundColor = when (status) {
        RequestStatus.APPROVED -> Color(0xFF00cc99)
        RequestStatus.DENIED -> Color(0xFFeb5757)
        else -> Color.Gray
    }

    Box(
        modifier = Modifier
            .padding(top = 8.dp)
            .background(color = backgroundColor, shape = RoundedCornerShape(4.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = when (status) {
                RequestStatus.APPROVED -> "Approved"
                RequestStatus.DENIED -> "Denied"
                else -> ""
            },
            fontSize = 16.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun ConfirmationDialog(
    action: Action?,
    onConfirm: (RequestStatus) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Are you sure you want to ${action?.name?.lowercase()} this request?") },
        confirmButton = {
            Button(
                onClick = {
                    val status = when (action) {
                        Action.APPROVE -> RequestStatus.APPROVED
                        Action.DENY -> RequestStatus.DENIED
                        else -> return@Button // No action, so don't do anything
                    }
                    onConfirm(status)
                }
            ) {
                Text("Yes")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text("No")
            }
        }
    )
}
