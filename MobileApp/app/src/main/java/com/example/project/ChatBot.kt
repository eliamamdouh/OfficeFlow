package com.example.project
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text

val lighterGray = Color(0xFFEBEBEB) // Lighter grey
val slightlyDarkerGreen = Color(0xFF86BC24) // Darker green
val whiteBackground = Color(0xFFFFFFFF)
val DarkGreen = Color(0xFF5cb373)


data class ChatMessage(
    val isBot: Boolean,
    val message: String
)
@Composable
fun ChatScreen() {
    var chatMessages by remember { mutableStateOf(
        listOf(
            ChatMessage(isBot = true, message = "Hello, how can I assist you today?")
        )
    )}
    var userInput by remember { mutableStateOf("") }
    var conversationStep by remember { mutableIntStateOf(0) } // To track conversation flow

    // Function to handle user input and generate bot responses
    fun handleUserInput(input: String) {
        val lowerCaseInput = input.lowercase()
        val response = when {
            lowerCaseInput.contains("thank you") -> {
                conversationStep = 0
                "I am happy to answer you anytime, Bye!"
            }
            conversationStep == 0 -> {
                if (lowerCaseInput.contains("i want to ask a quick question")) {
                    conversationStep = 1
                    "Yeah, go right ahead."
                } else {
                    "Sorry, I didn't understand. Please say 'I want to ask a quick question' to proceed."
                }
            }
            conversationStep == 1 -> {
                when {
                    lowerCaseInput.contains("changing work day") -> {
                        "You can go right ahead to the home page and choose the day you want to change and the day you want it to be changed to and submit the request for the change."
                    }
                    lowerCaseInput.contains("request status") -> {
                        "You can open the activity screen and you will find your request and based on three status options whether it's pending, accepted, or denied."
                    }
                    lowerCaseInput.contains("workload reminders") -> {
                        "There will be reminders every day that will alert you for any workload you have."
                    }
                    else -> "Sorry, I didn't understand that. Please choose one of the questions."
                }
            }
            else -> {
                conversationStep = 0
                "Sorry, I didn't understand that."
            }
        }

        chatMessages = chatMessages + ChatMessage(isBot = false, message = input)
        chatMessages = chatMessages + ChatMessage(isBot = true, message = response)
        userInput = ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(whiteBackground)
            .padding(16.dp)
    ) {
        // Title
        Text(
            text = "ChatBot",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .align(Alignment.CenterHorizontally)
        )

        // Scrollable chat messages
        Box(
            modifier = Modifier
                .weight(1f)
                .background(whiteBackground)
                .padding(bottom = 8.dp)
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                chatMessages.forEach { message ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 13.dp),
                        horizontalArrangement = if (message.isBot) Arrangement.Start else Arrangement.End
                    ) {
                        Box(
                            modifier = Modifier
                                .background(if (message.isBot) lighterGray.copy(alpha = 0.5f) else DarkGreen)
                                .padding(13.dp)
                                .clip(RoundedCornerShape(16.dp)) // Rounded corners for messages
                        ) {
                            Text(
                                text = message.message,
                                color = if (message.isBot) Color.Black else Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Start
                            )
                        }
                    }
                }
            }
        }

        // Input field with submit button inside
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .background(lighterGray, shape = RoundedCornerShape(50.dp))
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicTextField(
                        value = userInput,
                        onValueChange = { userInput = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .background(lighterGray, shape = RoundedCornerShape(50.dp)),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Send,
                            keyboardType = KeyboardType.Text
                        ),
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 20.sp
                        )
                    )
                    IconButton(
                        onClick = {
                            handleUserInput(userInput)
                        },
                        modifier = Modifier
                            .size(30.dp)
                            .background(slightlyDarkerGreen, shape = RoundedCornerShape(30.dp)) // Circular green background
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowUpward,
                            contentDescription = "Send",
                            tint = Color.White // White arrow
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    ChatScreen()
}


