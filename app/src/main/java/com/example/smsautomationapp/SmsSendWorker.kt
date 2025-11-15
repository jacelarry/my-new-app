package com.example.smsautomationapp

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.Data
import com.example.smsautomationapp.service.SendQueue
import com.example.smsautomationapp.network.SmsApi
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SmsSendWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val message = inputData.getString(KEY_MESSAGE) ?: return Result.failure()
        val recips = inputData.getStringArray(KEY_RECIPIENTS) ?: emptyArray()
        if (recips.isEmpty()) return Result.success()
        val api = Retrofit.Builder()
            .baseUrl("https://api.your-sms-provider.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SmsApi::class.java)
        val queue = SendQueue(api)
        queue.sendAll(recips.toList(), message)
        return Result.success()
    }

    companion object {
        const val KEY_MESSAGE = "msg"
        const val KEY_RECIPIENTS = "recips"
        fun buildData(message: String, recipients: List<String>) = Data.Builder()
            .putString(KEY_MESSAGE, message)
            .putStringArray(KEY_RECIPIENTS, recipients.toTypedArray())
            .build()
    }
}
