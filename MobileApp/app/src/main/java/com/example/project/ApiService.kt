package com.example.project

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

data class LoginRequest(
    val email: String?,
    val password: String?,
    val deviceToken: String?  // Add this field
)

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
    val message: String,
    val schedule: Map<String, Map<String, List<ScheduleDay>>>
)


data class ScheduleDay(
    val day: String,
    val location: String
)

data class TeamMembersResponse(val teamMembers: List<TeamMember>)

data class TeamMember(
    val userId: String,
    val name: String,
    val role: String,
    val schedules: Map<String, Map<String, List<ScheduleDay>>>

)
data class Request(
    val id: String,
    val timeAgo: String?,
    val description: String?,
    val userName:String?,
    val status: RequestStatus
)

enum class RequestStatus {
    PENDING,
    APPROVED,
    DENIED
}

data class Notification(

    val text: String,
)

data class CountUsersResponse(
    val homeCount: Int,
    val officeCount: Int,
    val officeCapacity: Int
)

data class GenerateScheduleRequest(
    val oddWeekOfficeDays: String,
    val evenWeekOfficeDays: String
)

data class GenerateScheduleResponse(
    val message: String
)


data class CountRequestsResponse(
    val totalRequests: Int,
    val acceptedCount: Int,
    val rejectedCount: Int,
    val pendingCount: Int,
    val acceptedPercentage: String,
    val rejectedPercentage: String,
    val pendingPercentage: String
)



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
    fun viewSchedule(@Header("Authorization") token: String): Call<ScheduleResponse>

    @GET("/users/TeamSchedule")
    fun viewScheduleForTeamMembers(
        @Header("Authorization") token: String,
        @Query("userId") userId: String

        ): Call<ScheduleResponse>

    @GET("/getTeamMembers")
    fun getTeamMembers(
        @Header("Authorization") token: String
    ): Call<TeamMembersResponse>

    @GET("users/view-requests")
    fun viewRequests(@Header("Authorization") token: String): Call<List<Request>>
    @GET("requests/view-requests")
    fun getRequests(@Header("Authorization") token: String): Call<List<Request>>

    @POST("users/cancel-request")
    fun cancelRequest(
        @Header("Authorization") token: String,
        @Body requestId: RequestId
    ): Call<CancelRequestResponse>

    @GET("viewNotifications")
    suspend fun getNotifications(
        @Header("Authorization") token: String
    ): Response<List<Notification>>

    @GET("countUsers")
    fun countUsers(@Header("Authorization") token: String): Call<CountUsersResponse>

    @GET("requests/countRequests")
    fun countRequests(@Header("Authorization") token: String): Call<CountRequestsResponse>


    @POST("users/accept-request")
    fun acceptRequest(
        @Header("Authorization") token: String,
        @Body requestId: RequestId
    ): Call<Void>

    @POST("users/reject-request")
    fun rejectRequest(
        @Header("Authorization") token: String,
        @Body requestId: RequestId
    ): Call<Void>

    @POST("users/generate-dynamicSchedule")
    fun generateDynamicSchedule(
        @Header("Authorization") token: String,
        @Body request: GenerateScheduleRequest
    ): Call<GenerateScheduleResponse>


}


data class RequestId(val requestId: String)
data class CancelRequestResponse(val message: String)
