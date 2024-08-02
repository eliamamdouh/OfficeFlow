import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.project.R
import kotlinx.coroutines.launch

@Composable
fun ManagerRequests() {
    var requests by remember {
        mutableStateOf(listOf(
            MgrRequest("Merna Ahmed", "8m ago", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec fringilla quam eu faci"),
            MgrRequest("Ali Mohammed", "10 days ago", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec fringilla quam eu faci"),
            MgrRequest("Mazen Abdullah", "15 days ago", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec fringilla quam eu faci"),
            MgrRequest("Merna Ahmed", "8m ago", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec fringilla quam eu faci"),
            MgrRequest("Ali Mohammed", "10 days ago", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec fringilla quam eu faci"),
            MgrRequest("Mazen Abdullah", "15 days ago", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec fringilla quam eu faci")
        ))
    }

    val scrollState = rememberScrollState()
    var showScrollToTop by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Update the visibility of the "Scroll to Top" button based on scroll position
    LaunchedEffect(scrollState.value) {
        showScrollToTop = scrollState.value > 300 // You can adjust this threshold
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
            .fillMaxWidth()
    ) {
        Column(//da el col bta3 el bta3a el 3ayza tetrefe3
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(35.dp))
                .padding(16.dp)
                //.padding(horizontal = 0.dp , vertical = 200.dp)
                .verticalScroll(scrollState) // Make column scrollable
        ) {
            Text(
                text = "Requests",
                fontSize = 25.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
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
                    onApprove = { updatedRequest ->
                        requests = requests.map { if (it == updatedRequest) it.copy(status = "Request Accepted") else it }
                    },
                    onDeny = { updatedRequest ->
                        requests = requests.map { if (it == updatedRequest) it.copy(status = "Request Rejected") else it }
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

        // Scroll to Top Button
        if (showScrollToTop) {
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        scrollState.scrollTo(0)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .size(80.dp)
                    .padding(16.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.arrowupgray), // Replace with your icon resource ID
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
    request: MgrRequest,
    onApprove: (MgrRequest) -> Unit,
    onDeny: (MgrRequest) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var currentAction by remember { mutableStateOf<Action?>(null) }
    var status by remember { mutableStateOf(request.status) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Are you sure you want to ${currentAction?.text} this request?") },
            confirmButton = {
                Button(
                    onClick = {
                        when (currentAction) {
                            Action.APPROVE -> {
                                onApprove(request.copy(status = "Request Accepted"))
                                status = "Request Accepted"
                            }
                            Action.DENY -> {
                                onDeny(request.copy(status = "Request Rejected"))
                                status = "Request Rejected"
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = request.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier
                    .padding(bottom = 4.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = request.timeAgo,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .alpha(0.5f)
            )
        }

        Text(
            text = request.description,
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (status.isEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
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
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .background(
                            color = if (status == "Request Accepted") Color(0xFF00cc99) else Color(0xFFeb5757),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = status,
                        fontSize = 16.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        status = ""
                        // You may need to also update the request in the parent composable if needed
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}

data class MgrRequest(
    val name: String,
    val timeAgo: String,
    val description: String,
    val status: String = ""
)

enum class Action(val text: String) {
    APPROVE("accept"), DENY("reject")
}
