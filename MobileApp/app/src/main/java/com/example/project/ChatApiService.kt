package com.example.project

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


data class ChatRequest(
    val prompt: String
)

data class ChatResponse(
    val response: String
)

interface ChatApiService {
    @POST("chat")
    fun sendChatMessage(@Body request: ChatRequest): Call<ChatResponse>
}
