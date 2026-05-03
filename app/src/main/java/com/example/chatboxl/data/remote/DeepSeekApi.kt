package com.example.chatboxl.data.remote

import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface DeepSeekApi {
    @POST("chat/completions")
    fun getChatResponse(
        @Header("Authorization") token: String,
        @Body chatRequest: ChatRequest
    ): Single<ChatResponse>
}