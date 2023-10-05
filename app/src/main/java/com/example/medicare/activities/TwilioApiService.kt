package com.example.medicare.activities

import okhttp3.Credentials
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface TwilioApiService {
    @FormUrlEncoded
    @POST("2010-04-01/Accounts/{accountSid}/Messages.json")
    fun sendSMS(
        @Path("accountSid") accountSid: String,
        @Field("To") toPhoneNumber: String,
        @Field("From") fromPhoneNumber: String,
        @Field("Body") message: String
    ): Call<Void>
}
fun createTwilioApiService(): TwilioApiService {
    val accountSid ="AC0dc5ab3e773a73754e063e0da86cba1b"
    val authToken = "242aecf13b1652243624a92ee5b7b2fb"

    val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .header("Authorization", Credentials.basic(accountSid, authToken))
            val request = requestBuilder.build()
            chain.proceed(request)
        }
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.twilio.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    return retrofit.create(TwilioApiService::class.java)
}