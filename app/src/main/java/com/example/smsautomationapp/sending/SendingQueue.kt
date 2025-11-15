package com.example.smsautomationapp.sending

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlin.math.min

/**
 * SendingQueue processes SendTask items concurrently with a max parallelism.
 * Provides retry with exponential backoff and jitter.
 */
class SendingQueue(
    private val scope: CoroutineScope,
    private val maxConcurrent: Int = 4,
    private val maxRetries: Int = 3,
    private val baseDelayMs: Long = 500L,
    private val onProgress: (ProgressSnapshot) -> Unit = {},
    private val onTaskResult: (SendTask, Result<Unit>) -> Unit = { _, _ -> }
) {
    private val semaphore = Semaphore(maxConcurrent)
    private val channel = Channel<SendTask>(Channel.UNLIMITED)

    private var total = 0
    private var succeeded = 0
    private var failed = 0

    private var workerJob: Job? = null

    fun start() {
        if (workerJob != null) return
        workerJob = scope.launch(Dispatchers.IO) {
            for (task in channel) {
                semaphore.withPermit { processTask(task) }
            }
        }
    }

    suspend fun enqueue(task: SendTask) {
        total++
        channel.send(task)
        emitProgress()
    }

    suspend fun enqueueAll(tasks: List<SendTask>) {
        total += tasks.size
        tasks.forEach { channel.send(it) }
        emitProgress()
    }

    private suspend fun processTask(task: SendTask) {
        var attempt = 0
        while (attempt <= maxRetries) {
            val result = runCatching { task.action() }
            if (result.isSuccess) {
                succeeded++
                onTaskResult(task, Result.success(Unit))
                emitProgress()
                return
            } else {
                attempt++
                if (attempt > maxRetries) {
                    failed++
                    onTaskResult(task, Result.failure(result.exceptionOrNull() ?: Exception("Unknown error")))
                    emitProgress()
                    return
                } else {
                    val backoff = computeBackoff(attempt)
                    delay(backoff)
                }
            }
        }
    }

    private fun computeBackoff(attempt: Int): Long {
        val exp = baseDelayMs * (1 shl (attempt - 1))
        val capped = min(exp, 10_000L)
        val jitter = (0..200).random()
        return capped + jitter
    }

    private fun emitProgress() {
        onProgress(ProgressSnapshot(total, succeeded, failed, inFlight = total - (succeeded + failed)))
    }

    fun stop() {
        workerJob?.cancel()
        workerJob = null
    }
}

data class SendTask(
    val id: String,
    val action: suspend () -> Unit
)

data class ProgressSnapshot(
    val total: Int,
    val succeeded: Int,
    val failed: Int,
    val inFlight: Int
) {
    val completed: Int get() = succeeded + failed
    val percent: Int get() = if (total == 0) 0 else (completed * 100 / total)
}
