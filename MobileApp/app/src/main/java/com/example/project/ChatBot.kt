package com.example.project

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

val lighterGray = Color(0xFFEBEBEB) // Lighter grey
val slightlyDarkerGreen = Color(0xFF86BC24) // Darker green
val darkGreen = Color(0xFF5CB373)
val placeholderBackground = Color(0xFFBDBDBD) // Placeholder background color
val borderColor = Color(0xFFE8E8E8) // Border color for the text input field

data class ChatMessage(
    val isBot: Boolean,
    val message: String
)

@Composable
fun ChatScreen() {
    var chatMessages by remember {
        mutableStateOf(
            listOf(ChatMessage(isBot = true, message = "Hello, how can I assist you today?"))
        )
    }
    var userInput by remember { mutableStateOf("") }

    // Function to handle user input and generate bot responses
    fun handleUserInput(
        input: String,
        chatMessages: List<ChatMessage>,
        updateMessages: (List<ChatMessage>) -> Unit
    ) {
        // Add user message to the list
        updateMessages(chatMessages + ChatMessage(isBot = false, message = input))

        // Prepare the request
        val chatRequest = ChatRequest(prompt = input)

        // Send the request to the backend
        RetrofitClient2.chatApiService.sendChatMessage(chatRequest).enqueue(object : Callback<ChatResponse> {
            override fun onResponse(call: Call<ChatResponse>, response: Response<ChatResponse>) {
                val botResponse = if (response.isSuccessful) {
                    response.body()?.response ?: "Sorry, I didn't understand that."
                } else {
                    "Error: ${response.errorBody()?.string()}"
                }
                updateMessages(chatMessages + ChatMessage(isBot = true, message = botResponse))
            }

            override fun onFailure(call: Call<ChatResponse>, t: Throwable) {
                updateMessages(chatMessages + ChatMessage(isBot = true, message = "Error: ${t.message}"))
            }
        })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(lighterGray) // Grey page background
            .padding(start = 0.dp, top = 15.dp, bottom = 26.dp) // Padding around the rounded box
    ) {
        // Rounded box container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color.White)
                .padding(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 25.dp) // Ensure space for the input field
            ) {
                // Title
                Text(
                    text = "ChatBot",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier
                        .padding(bottom = 18.dp)
                        .align(Alignment.CenterHorizontally)
                )

                // Chat messages and input field
                Column(
                    modifier = Modifier
                        .fillMaxWidth() // Ensure space for the input field
                ) {
                    // Scrollable chat messages
                    Box(
                        modifier = Modifier
                            .weight(1f) // Takes up available space
                            .background(Color.White) // Ensure messages are on white background
                    ) {
                        Column(
                            modifier = Modifier.verticalScroll(rememberScrollState())
                        ) {
                            chatMessages.forEach { message ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 10.dp),
                                    horizontalArrangement = if (message.isBot) Arrangement.Start else Arrangement.End
                                ) {
                                    ChatBubble(
                                        isBot = message.isBot,
                                        message = message.message
                                    )
                                }
                            }
                        }
                    }

                    // Input field with submit button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(49.dp) // Set the height as specified
                            .background(Color(0xFFF6F6F6)) // Background color
                            .border(1.dp, borderColor, RoundedCornerShape(50.dp))
                            .clip(RoundedCornerShape(50.dp))
                            .padding(horizontal = 20.dp) // Padding to ensure input and button are properly aligned
                    ) {
                        BasicTextField(
                            value = userInput,
                            onValueChange = { userInput = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 56.dp, top = 8.dp, bottom = 8.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Send,
                                keyboardType = KeyboardType.Text
                            ),
                            textStyle = TextStyle(
                                fontSize = 18.sp,
                                color = Color.Black
                            )
                        )
                        if (userInput.isEmpty()) {
                            Text(
                                text = "Message Here...",
                                color = placeholderBackground,
                                fontSize = 18.sp,
                                modifier = Modifier
                                    .padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
                                    .align(Alignment.CenterStart)
                            )
                        }
                        IconButton(
                            onClick = {
                                if (userInput.isNotEmpty()) {
                                    handleUserInput(userInput, chatMessages) { updatedMessages ->
                                        chatMessages = updatedMessages
                                    }
                                    userInput = ""
                                }
                            },
                            modifier = Modifier
                                .size(34.dp)
                                .background(
                                    if (userInput.isEmpty()) Color.Gray else slightlyDarkerGreen,
                                    shape = RoundedCornerShape(50)
                                )
                                .align(Alignment.CenterEnd)
                                .padding(0.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowUpward,
                                contentDescription = "Send",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(isBot: Boolean, message: String) {
    val bubbleShape = if (isBot) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 0.dp, bottomEnd = 20.dp)
    } else {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 20.dp, bottomEnd = 0.dp)
    }

    Box(
        modifier = Modifier
            .clip(bubbleShape)
            .background(if (isBot) lighterGray.copy(alpha = 0.5f) else darkGreen)
            .border(1.dp, borderColor, bubbleShape)
            .padding(16.dp)
    ) {
        Text(
            text = message,
            color = if (isBot) Color.Black else Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Start
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    ChatScreen()
}
