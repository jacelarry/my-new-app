package com.example.smsautomationapp.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

data class SmsRequest(val to: String, val message: String)
data class SmsResponse(val success: Boolean, val id: String?, val error: String?)

interface SmsApi {
    @POST("/send")
    suspend fun sendSms(@Body req: SmsRequest): Response<SmsResponse>
}

