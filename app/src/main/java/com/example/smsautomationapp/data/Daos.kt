package com.example.smsautomationapp.data

import androidx.paging.PagingSource
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CampaignDao {
    @Insert
    suspend fun insert(campaign: SendCampaign): Long

    @Update
    suspend fun update(campaign: SendCampaign)

    @Query("SELECT * FROM campaigns ORDER BY startTime DESC")
    fun getAllPaged(): PagingSource<Int, SendCampaign>

    @Query("SELECT * FROM campaigns ORDER BY startTime DESC LIMIT 10")
    fun getRecentFlow(): Flow<List<SendCampaign>>

    @Query("SELECT * FROM campaigns WHERE id = :id")
    suspend fun getById(id: Long): SendCampaign?
}

@Dao
interface SendResultDao {
    @Insert
    suspend fun insert(result: SendResult)

    @Insert
    suspend fun insertAll(results: List<SendResult>)

    @Query("SELECT * FROM send_results WHERE campaignId = :campaignId ORDER BY timestamp DESC")
    fun getResultsByCampaign(campaignId: Long): PagingSource<Int, SendResult>

    @Query("SELECT * FROM send_results WHERE campaignId = :campaignId AND success = 0")
    suspend fun getFailedRecipients(campaignId: Long): List<SendResult>

    @Query("SELECT COUNT(*) FROM send_results WHERE campaignId = :campaignId AND success = 1")
    suspend fun getSuccessCount(campaignId: Long): Int

    @Query("SELECT COUNT(*) FROM send_results WHERE campaignId = :campaignId AND success = 0")
    suspend fun getFailedCount(campaignId: Long): Int
}

@Dao
interface MetricsDao {
    @Insert
    suspend fun insert(metrics: SendMetrics)

    @Query("SELECT * FROM metrics WHERE campaignId = :campaignId")
    suspend fun getByCampaign(campaignId: Long): SendMetrics?

    @Query("SELECT AVG(avgLatencyMs) FROM metrics WHERE timestamp > :since")
    suspend fun getAvgLatencySince(since: Long): Float?
}
