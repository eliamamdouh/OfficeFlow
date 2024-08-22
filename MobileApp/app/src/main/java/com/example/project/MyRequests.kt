package com.example.project

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private fun fetchRequests(token: String, context: Context, navController: NavHostController, onResult: (List<Request>) -> Unit) {
    val apiService = RetrofitClient.apiService
    Log.d("token mn fetch:","$token")

    apiService.viewRequests("Bearer $token")
        .enqueue(object : Callback<List<Request>> {
            override fun onResponse(call: Call<List<Request>>, response: Response<List<Request>>) {
                if (response.isSuccessful) {
                    val requests = response.body() ?: emptyList()
                    onResult(requests)
                    Log.d("req:","$requests")
                } else { if (response.code() == 401 ) {
                    Log.d("respCode:","$response.code()")
                    handleTokenExpiration(navController)
                }
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error occurred"
                    Log.e("Requests", "Error fetching requests: $errorMessage")
                    // Show a user-friendly message
                    Toast.makeText(context, "Failed to fetch requests: $errorMessage", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<Request>>, t: Throwable) {
                Log.e("Requests", "Failure fetching requests: ${t.localizedMessage}", t)
                // Show a user-friendly message
                Toast.makeText(context, "Failed to fetch requests: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })
}


@Composable
fun MyRequests(context: Context, navController: NavHostController) {
    // Scroll stuff
    val scrollState = rememberScrollState()
    var showScrollToTop by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(scrollState.value) {
        showScrollToTop = scrollState.value > 300
    }

    var requests by remember { mutableStateOf(emptyList<Request>()) }

    // Retrieve the token from SharedPreferences
    val token = PreferencesManager.getTokenFromPreferences(context)

    // Fetch requests when the composable is first launched
    LaunchedEffect(Unit) {
        token?.let {
            fetchRequests(it, context, navController) { fetchedRequests ->
                requests = fetchedRequests
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
            .fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(35.dp))
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            Text(
                text = "My Requests",
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
                RequestItem(
                    request = request, navController,
                    onRequestCancelled = {
                        requests = requests.filter { it.id != request.id }
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

        if (showScrollToTop) {
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        scrollState.scrollTo(0)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter) //could be bottom end
                    .size(80.dp)
                    .padding(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.arrowupgray),
                    contentDescription = "Scroll to Top",
                    modifier = Modifier.size(40.dp)
                        .alpha(0.6f)
                )
            }
        }
    }
}


@Composable
fun RequestItem(
    request: Request, navController: NavHostController,
    onRequestCancelled: () -> Unit
) {
    val context = LocalContext.current
    val token = PreferencesManager.getTokenFromPreferences(context) ?: return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = request.timeAgo ?: "Unknown time",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier
                .padding(bottom = 14.dp)
                .alpha(0.5f)
        )
        Text(
            text = request.description ?: "No description available",
            fontSize = 18.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 15.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            val status = request.status ?: RequestStatus.PENDING
            when (status) {
                RequestStatus.PENDING -> {
                    Image(
                        painter = painterResource(id = R.drawable.trash),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                cancelRequest(token, request.id, context, navController) { success ->
                                    if (success) {
                                        onRequestCancelled()
                                    }
                                }
                            },
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Cancel Request",
                        fontSize = 18.sp,
                        color = Color(0xFF86BC24),
                        modifier = Modifier.clickable {
                            cancelRequest(token, request.id, context, navController) { success ->
                                if (success) {
                                    onRequestCancelled()
                                }
                            }
                        }
                    )
                }
                RequestStatus.APPROVED -> {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF00CC99))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Approved",
                            fontSize = 14.sp,
                            color = Color.White,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
                RequestStatus.DENIED -> {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Red)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Denied",
                            fontSize = 14.sp,
                            color = Color.White,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}

private fun cancelRequest(token: String, requestId: String, context: Context, navController: NavHostController, onResult: (Boolean) -> Unit) {
    val apiService = RetrofitClient.apiService

    apiService.cancelRequest("Bearer $token", RequestId(requestId))
        .enqueue(object : Callback<CancelRequestResponse> {
            override fun onResponse(call: Call<CancelRequestResponse>, response: Response<CancelRequestResponse>) {
                if (response.isSuccessful) {
                    onResult(true)
                    Log.d("CancelRequest", "Request cancelled successfully")
                }  else { if (response.code() == 401 ) {
                    Log.d("respCode:","$response.code()")
                    handleTokenExpiration(navController)
                } else{
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error occurred"
                    Log.e("CancelRequest", "Error cancelling request: $errorMessage")
                    Toast.makeText(context, "Failed to cancel request: $errorMessage", Toast.LENGTH_LONG).show()
                    onResult(false)
                }}
            }

            override fun onFailure(call: Call<CancelRequestResponse>, t: Throwable) {
                Log.e("CancelRequest", "Failure cancelling request: ${t.localizedMessage}", t)
                Toast.makeText(context, "Failed to cancel request: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
                onResult(false)
            }
        })
}