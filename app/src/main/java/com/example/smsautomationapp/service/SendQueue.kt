package com.example.smsautomationapp.service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import retrofit2.Response
import kotlin.math.min
import kotlin.random.Random
import com.example.smsautomationapp.network.SmsApi
import com.example.smsautomationapp.network.SmsRequest
import com.example.smsautomationapp.network.SmsResponse

class SendQueue(
    private val api: SmsApi,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO),
    private val concurrency: Int = 4,
    private val maxRetries: Int = 3
) {
    private val sem = Semaphore(concurrency)
    @Volatile private var cancelled = false

    data class Result(
        val to: String,
        val success: Boolean,
        val attempts: Int,
        val error: String?
    )

    suspend fun sendAll(
        recipients: List<String>,
        message: String,
        onEach: (Result, index: Int, total: Int) -> Unit = { _, _, _ -> }
    ): List<Result> {
        val jobs = mutableListOf<Job>()
        val results = ArrayList<Result>(recipients.size)
        recipients.forEachIndexed { idx, to ->
            val job = scope.launch {
                val r = sendWithRetries(to, message)
                synchronized(results) { results.add(r) }
                onEach(r, idx + 1, recipients.size)
            }
            jobs.add(job)
            if (cancelled) return@forEachIndexed
        }
        jobs.forEach { it.join() }
        return results
    }

    fun cancel() { cancelled = true; scope.cancel() }

    private suspend fun sendWithRetries(to: String, message: String): Result {
        var attempt = 0
        var lastError: String? = null
        while (!cancelled && attempt <= maxRetries) {
            attempt++
            try {
                val resp: Response<SmsResponse> = sem.withPermit { api.sendSms(SmsRequest(to, message)) }
                if (resp.isSuccessful && resp.body()?.success == true) {
                    return Result(to, true, attempt, null)
                } else {
                    lastError = resp.body()?.error ?: "HTTP ${'$'}{resp.code()}"
                }
            } catch (ex: Exception) {
                lastError = ex.message
            }
            val backoff = computeBackoff(attempt)
            delay(backoff)
        }
        if (cancelled) return Result(to, false, attempt, "Cancelled")
        return Result(to, false, attempt, lastError)
    }

    private fun computeBackoff(attempt: Int): Long {
        val base = 600L
        val pow = min(attempt, 6)
        val back = base * (1L shl (pow - 1))
        val capped = min(back, 8000L)
        val jitter = Random.nextLong(-150, 150)
        return (capped + jitter).coerceAtLeast(200L)
    }
}

