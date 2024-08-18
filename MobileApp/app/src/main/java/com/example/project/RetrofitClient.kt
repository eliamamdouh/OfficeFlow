package com.example.project

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
  private const val BASE_URL = "http://10.0.2.2:3000/api/"


    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
object RetrofitClient2 {
    private const val BASE_URL = "http://10.0.2.2:5001/" // Replace with your second backend URL

    val chatApiService: ChatApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ChatApiService::class.java) // Corrected this line
    }
}
