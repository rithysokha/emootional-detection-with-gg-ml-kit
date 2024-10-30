package com.emotion.detector.api

import com.emotion.detector.model.ApiResponse
import retrofit2.http.GET

interface ApiService  {
@GET("greeting")
suspend fun loadGreet(): ApiResponse<String>
}