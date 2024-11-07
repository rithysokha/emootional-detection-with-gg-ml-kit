package com.emotion.detector.api

import com.emotion.detector.model.ApiResponse
import com.emotion.detector.model.History
import com.emotion.detector.model.Profile
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService  {
@GET("greeting")
suspend fun loadGreet(): ApiResponse<String>

@GET("profiles/{id}")
suspend fun loadProfile(@Path("id") id: Int): ApiResponse<Profile>

@GET("history")
suspend fun loadHistory(): ApiResponse<List<History>>
}