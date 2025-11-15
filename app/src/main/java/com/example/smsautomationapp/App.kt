package com.example.smsautomationapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.room.Room
import com.example.smsautomationapp.data.AppDatabase
import com.google.android.material.color.DynamicColors

class App : Application() {
    companion object {
        const val CHANNEL_ID_SENDING = "sms_sending"
        lateinit var database: AppDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        createNotificationChannels()
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "sms_automation_db"
        ).build()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val sendingChannel = NotificationChannel(
                CHANNEL_ID_SENDING,
                "SMS Sending Progress",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows progress of bulk SMS sending operations"
                setShowBadge(false)
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(sendingChannel)
        }
    }
}
