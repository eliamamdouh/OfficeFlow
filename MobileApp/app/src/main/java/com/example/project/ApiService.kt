package com.example.project

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

data class LoginRequest(val email: String?, val password: String?)
data class LoginResponse(
    val message: String,
    val userId: String,
    val token: String,
    val role: String,
)

data class UserInfoResponse(
    val name: String,
    val todaySchedule: String
)

data class SubmitRequest(
    val newDate: String,
    val dayToChange: String,
    val reason: String
)

data class SubmitRequestResponse(
    val message: String,
    val requestId: String
)

data class ScheduleResponse(
    val schedule: Map<String, List<ScheduleDay>>
)

data class ScheduleDay(
    val day: String,
    val location: String
)

data class Request(
    val id: String, // Assuming there's an ID field in your response
    val timeAgo: String,
    val description: String,
    val status: RequestStatus
)

enum class RequestStatus {
    PENDING,
    APPROVED,
    DENIED
}



interface ApiService {
    @POST("users/login")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>
    @GET("users/{userId}")
    fun getUserInfo(@Path("userId") userId: String): Call<UserInfoResponse>

    @POST("users/submit-request")
    fun submitRequest(
        @Header("Authorization") token: String,
        @Body request: SubmitRequest
    ): Call<SubmitRequestResponse>

    @GET("users/schedule")
//    fun viewSchedule(@Query("username") username: String): Call<ScheduleResponse>
    fun viewSchedule(@Query("userId") userId: String): Call<ScheduleResponse>

