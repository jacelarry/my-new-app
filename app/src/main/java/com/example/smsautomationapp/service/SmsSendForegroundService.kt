package com.example.smsautomationapp.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.smsautomationapp.App
import com.example.smsautomationapp.MainActivity
import com.example.smsautomationapp.R
import com.example.smsautomationapp.data.AppDatabase
import com.example.smsautomationapp.data.SendCampaign
import com.example.smsautomationapp.data.SendMetrics
import com.example.smsautomationapp.data.SendResult
import com.example.smsautomationapp.network.SmsApi
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SmsSendForegroundService : Service() {
    companion object {
        const val ACTION_START = "START_SENDING"
        const val ACTION_CANCEL = "CANCEL_SENDING"
        const val EXTRA_MESSAGE = "message"
        const val EXTRA_RECIPIENTS = "recipients"
        const val NOTIFICATION_ID = 1001

        fun startSending(context: Context, message: String, recipients: List<String>) {
            val intent = Intent(context, SmsSendForegroundService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_MESSAGE, message)
                putStringArrayListExtra(EXTRA_RECIPIENTS, ArrayList(recipients))
            }
            context.startForegroundService(intent)
        }

        fun cancelSending(context: Context) {
            val intent = Intent(context, SmsSendForegroundService::class.java).apply { action = ACTION_CANCEL }
            context.startService(intent)
        }
    }

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var sendQueue: SendQueue? = null
    private var currentJob: Job? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val message = intent.getStringExtra(EXTRA_MESSAGE) ?: ""
                val recipients = intent.getStringArrayListExtra(EXTRA_RECIPIENTS) ?: emptyList()
                startForeground(NOTIFICATION_ID, createNotification(0, recipients.size, "Starting..."))
                startSending(message, recipients)
            }
            ACTION_CANCEL -> cancelSending()
        }
        return START_NOT_STICKY
    }

    private fun startSending(message: String, recipients: List<String>) {
        currentJob = serviceScope.launch {
            val db = App.database
            val campaign = SendCampaign(
                message = message,
                totalRecipients = recipients.size,
                status = "IN_PROGRESS"
            )
            val campaignId = db.campaignDao().insert(campaign)

            val api = Retrofit.Builder()
                .baseUrl("https://api.your-sms-provider.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SmsApi::class.java)

            sendQueue = SendQueue(api)
            val latencies = mutableListOf<Long>()
            val failureReasons = mutableMapOf<String, Int>()

            val results = sendQueue!!.sendAll(recipients, message) { result, index, total ->
                val progress = (index * 100) / total
                updateNotification(index, total, "Sending $index/$total ($progress%)")

                latencies.add(result.attempts * 600L)
                result.error?.let { error -> failureReasons[error] = failureReasons.getOrDefault(error, 0) + 1 }

                launch {
                    db.sendResultDao().insert(
                        SendResult(
                            campaignId = campaignId,
                            recipient = result.to,
                            success = result.success,
                            attempts = result.attempts,
                            error = result.error,
                            latencyMs = result.attempts * 600L
                        )
                    )
                }
            }

            val successCount = results.count { it.success }
            val failedCount = results.count { !it.success && it.error != "Cancelled" }
            val cancelledCount = results.count { it.error == "Cancelled" }

            db.campaignDao().update(
                campaign.copy(
                    id = campaignId,
                    successCount = successCount,
                    failedCount = failedCount,
                    cancelledCount = cancelledCount,
                    endTime = System.currentTimeMillis(),
                    status = "COMPLETED"
                )
            )

            if (latencies.isNotEmpty()) {
                db.metricsDao().insert(
                    SendMetrics(
                        campaignId = campaignId,
                        avgLatencyMs = latencies.average().toLong(),
                        maxLatencyMs = latencies.maxOrNull() ?: 0,
                        minLatencyMs = latencies.minOrNull() ?: 0,
                        totalAttempts = results.sumOf { it.attempts },
                        retryRate = results.count { it.attempts > 1 }.toFloat() / results.size,
                        topFailureReason = failureReasons.maxByOrNull { it.value }?.key
                    )
                )
            }

            updateNotification(recipients.size, recipients.size, "Completed: $successCount sent, $failedCount failed")
            delay(1500)
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    private fun cancelSending() {
        sendQueue?.cancel()
        currentJob?.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotification(current: Int, total: Int, text: String): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val cancelPendingIntent = PendingIntent.getService(
            this, 1, Intent(this, SmsSendForegroundService::class.java).apply { action = ACTION_CANCEL },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        return NotificationCompat.Builder(this, App.CHANNEL_ID_SENDING)
            .setContentTitle("Sending SMS")
            .setContentText(text)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setProgress(total, current, false)
            .setContentIntent(pendingIntent)
            .addAction(R.mipmap.ic_launcher, "Cancel", cancelPendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun updateNotification(current: Int, total: Int, text: String) {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager?
        nm?.notify(NOTIFICATION_ID, createNotification(current, total, text))
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }
}
