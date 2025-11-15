package com.example.smsautomationapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import java.util.Date

@Entity(tableName = "campaigns")
data class SendCampaign(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val message: String,
    val totalRecipients: Int,
    val successCount: Int = 0,
    val failedCount: Int = 0,
    val cancelledCount: Int = 0,
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long? = null,
    val status: String = "PENDING" // PENDING, IN_PROGRESS, COMPLETED, CANCELLED
)

@Entity(tableName = "send_results")
data class SendResult(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val campaignId: Long,
    val recipient: String,
    val success: Boolean,
    val attempts: Int,
    val error: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val latencyMs: Long = 0
)

@Entity(tableName = "metrics")
data class SendMetrics(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val campaignId: Long,
    val avgLatencyMs: Long,
    val maxLatencyMs: Long,
    val minLatencyMs: Long,
    val totalAttempts: Int,
    val retryRate: Float,
    val topFailureReason: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
