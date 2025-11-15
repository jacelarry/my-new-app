package com.example.smsautomationapp

import android.util.Base64
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class DarajaApi(
    private val consumerKey: String,
    private val consumerSecret: String,
    private val passkey: String,
    private val tillNumber: String
) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    // TODO: Add the correct Daraja API endpoints
    private val authUrl = "https://sandbox.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials"
    private val stkPushUrl = "https://sandbox.safaricom.co.ke/mpesa/stkpush/v1/processrequest"

    private suspend fun getAuthToken(): String? {
        val credentials = "$consumerKey:$consumerSecret"
        val encodedCredentials = Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)

        val request = Request.Builder()
            .url(authUrl)
            .addHeader("Authorization", "Basic $encodedCredentials")
            .build()

        return withContext(Dispatchers.IO) {
            try {
                val response: Response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val json = JSONObject(response.body!!.string())
                    json.getString("access_token")
                } else {
                    Log.e("DarajaApi", "Auth failed: ${'$'}{response.code} ${'$'}{response.message}")
                    null
                }
            } catch (t: Throwable) {
                Log.e("DarajaApi", "Auth request error", t)
                null
            }
        }
    }

    suspend fun initiateStkPush(phoneNumber: String, amount: Int, partyB: String): Boolean {
        val token = getAuthToken() ?: return false

        val timestamp = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())
        val password = Base64.encodeToString("${'$'}tillNumber${'$'}{passkey}${'$'}timestamp".toByteArray(), Base64.NO_WRAP)

        val payload = JSONObject()
            .put("BusinessShortCode", tillNumber)
            .put("Password", password)
            .put("Timestamp", timestamp)
            .put("TransactionType", "CustomerPayBillOnline")
            .put("Amount", amount)
            .put("PartyA", phoneNumber)
            .put("PartyB", partyB)
            .put("PhoneNumber", phoneNumber)
            // TODO: You will need a publicly accessible URL for the callback
            .put("CallBackURL", "https://your-callback-url.com/callback")
            .put("AccountReference", "SmsAutomationApp")
            .put("TransactionDesc", "Payment for goods")
            .toString()

        val request = Request.Builder()
            .url(stkPushUrl)
            .addHeader("Authorization", "Bearer ${'$'}token")
            .post(payload.toRequestBody("application/json".toMediaType()))
            .build()

        return withContext(Dispatchers.IO) {
            try {
                val response: Response = client.newCall(request).execute()
                if (!response.isSuccessful) {
                    Log.e("DarajaApi", "STK push failed: ${'$'}{response.code} ${'$'}{response.message}")
                }
                response.isSuccessful
            } catch (t: Throwable) {
                Log.e("DarajaApi", "STK request error", t)
                false
            }
        }
    }
}