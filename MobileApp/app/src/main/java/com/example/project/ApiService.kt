package com.example.project

import com.google.firebase.firestore.auth.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

data class LoginRequest(val email: String?, val password: String?)
data class LoginResponse(
    val message: String,
    val userId: String
)

data class UserInfoResponse(
    val name: String,
    val todaySchedule: String
)


interface ApiService {
    @POST("users/login")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>
    @GET("users/{userId}")
    fun getUserInfo(@Path("userId") userId: String): Call<UserInfoResponse>
}
