package com.example.medicare.activities


import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface MedichatbotApi {
    @Headers("Content-Type: application/json")
    @POST("api/v1/chat/startConversation")
    suspend fun postQuestion(@Body question: QuestionPayload): MedichatbotResponse
}
data class QuestionPayload(val question: String)


data class MedichatbotResponse(val llm_response: String, val articles: List<Article>)

// Assuming the Article data class reflects the structure of articles in your response
data class Article(val title: String, val url: String, val authors: List<String>, val year: String)

fun createMedichatbotApi(): MedichatbotApi {
    val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS) // Adjust timeout values as needed
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://medicareapi3.onrender.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    return retrofit.create(MedichatbotApi::class.java)
}
