package com.example.smsautomationapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [SendCampaign::class, SendResult::class, SendMetrics::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun campaignDao(): CampaignDao
    abstract fun sendResultDao(): SendResultDao
    abstract fun metricsDao(): MetricsDao
}
