package com.example.project

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private fun fetchRequests(token: String, context: Context, onResult: (List<Request>) -> Unit) {
    val apiService = RetrofitClient.apiService
    Log.d("token mn fetch:","$token")

    apiService.viewRequests("Bearer $token")
        .enqueue(object : Callback<List<Request>> {
        override fun onResponse(call: Call<List<Request>>, response: Response<List<Request>>) {
            if (response.isSuccessful) {
                val requests = response.body() ?: emptyList()
                onResult(requests)
                Log.d("req:","$requests")
            } else {
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
fun MyRequests(context: Context) {
    var requests by remember { mutableStateOf(emptyList<Request>()) }

    // Retrieve the token from SharedPreferences
    val token = PreferencesManager.getTokenFromPreferences(context)

    // Fetch requests when the composable is first launched
    LaunchedEffect(Unit) {
        token?.let {
            fetchRequests(it, context) { fetchedRequests ->
                requests = fetchedRequests
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))//(0xFFF8F8F8)
            .fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(35.dp))
                .padding(16.dp)
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
                    request = request,
                    onCancelRequest = {
                        requests = requests.filter { it != request }
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

@Composable
fun RequestItem(
    request: Request,
    onCancelRequest: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Display a fallback value if timeAgo is null
        Text(
            text = request.timeAgo ?: "Unknown time",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier
                .padding(bottom = 4.dp)
                .alpha(0.5f)
        )
        // Display a fallback value if description is null
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
            val status = request.status ?: RequestStatus.PENDING // Provide a fallback value or handle null case
            when (status) {
                RequestStatus.PENDING -> {
                    Image(
                        painter = painterResource(id = R.drawable.trash),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onCancelRequest() },
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Cancel Request",
                        fontSize = 18.sp,
                        color = Color(0xFF86BC24),
                        modifier = Modifier.clickable { onCancelRequest() }
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