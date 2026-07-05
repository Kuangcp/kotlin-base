package learn.coroutines

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.system.measureTimeMillis
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds

private val logger = KotlinLogging.logger {}

class WebClientTest {

    private val client = HttpClient(CIO)

    private data class RequestResult(
        val index: Int,
        val requestId: String,
        val success: Boolean,
        val body: String,
        val elapsedMs: Long
    )

    private suspend fun HttpClient.fetchWithTimeout(
        url: String,
        timeoutMs: Long,
    ): String = withTimeout(timeoutMs.milliseconds) {
        get(url) {
            timeout {
                requestTimeoutMillis = timeoutMs
                connectTimeoutMillis = timeoutMs
                socketTimeoutMillis = timeoutMs
            }
        }.bodyAsText()
    }

    private suspend fun executeRequest(
        url: String,
        index: Int,
        timeoutMs: Long,
        startTime: Long,
        semaphore: Semaphore,
    ): RequestResult = semaphore.withPermit {
        val requestId = UUID.randomUUID().toString().take(8)
        val elapsed = { System.currentTimeMillis() - startTime }

        runCatching {
            val body = client.fetchWithTimeout(url, timeoutMs)
            logger.info { "[${requestId}] 请求 #${index + 1} 完成 (${elapsed()}ms)" }
            RequestResult(index, requestId, true, body, elapsed())
        }.getOrElse { e ->
            logger.warn { "[${requestId}] 请求 #${index + 1} 失败: ${e.message} (${elapsed()}ms)" }
            RequestResult(index, requestId, false, "FAILED_${index + 1}", elapsed())
        }
    }

    @Test
    fun `http get with timeout`() = runBlocking {
        val totalTimeoutMs = 20000L    // 总超时 20秒
        val perRequestTimeoutMs = 5000L // 单次超时 5秒
        val concurrency = 2             // 最大并发数
        val totalRequests = 8           // 总请求数
        val url = "https://jsonplaceholder.typicode.com/posts/1"
        val semaphore = Semaphore(concurrency)

        logger.info { "开始 $totalRequests 次请求，并发=$concurrency，单次=${perRequestTimeoutMs}ms，总=${totalTimeoutMs}ms" }

        val results: List<RequestResult>
        val testStartTime = System.currentTimeMillis()
        val totalTime = measureTimeMillis {
            results = withTimeoutOrNull(totalTimeoutMs.milliseconds) {
                coroutineScope {
                    List(totalRequests) { index ->
                        async {
                            executeRequest(url, index, perRequestTimeoutMs, testStartTime, semaphore)
                        }
                    }.awaitAll()
                }
            } ?: emptyList()
        }

        logger.info { "完成 ${results.size}/$totalRequests，耗时=${totalTime}ms" }
        results.forEach { r ->
            logger.info { "  [${r.requestId}] #${r.index + 1} success=${r.success} ${r.elapsedMs}ms" }
        }

        assertTrue(results.isNotEmpty())
        client.close()
    }

    @Test
    fun `http get with timeout retry`() = runBlocking {
        val totalTimeoutMs = 20000L
        val perRequestTimeoutMs = 5000L
        val maxRetries = 2
        val concurrency = 2
        val totalRequests = 8
        val url = "https://jsonplaceholder.typicode.com/posts/1"
        val semaphore = Semaphore(concurrency)

        logger.info { "开始 $totalRequests 次请求(最多重试${maxRetries}次)，并发=$concurrency" }

        val results: List<RequestResult>
        val testStartTime = System.currentTimeMillis()
        val totalTime = measureTimeMillis {
            results = withTimeoutOrNull(totalTimeoutMs.milliseconds) {
                coroutineScope {
                    List(totalRequests) { index ->
                        async {
                            executeRequestWithRetry(url, index, perRequestTimeoutMs, testStartTime, semaphore, maxRetries)
                        }
                    }.awaitAll()
                }
            } ?: emptyList()
        }

        logger.info { "完成 ${results.size}/$totalRequests，耗时=${totalTime}ms" }
        results.forEach { r ->
            logger.info { "  [${r.requestId}] #${r.index + 1} success=${r.success} ${r.elapsedMs}ms" }
        }

        assertTrue(results.isNotEmpty())
        client.close()
    }

    private suspend fun executeRequestWithRetry(
        url: String,
        index: Int,
        timeoutMs: Long,
        startTime: Long,
        semaphore: Semaphore,
        maxRetries: Int,
    ): RequestResult = semaphore.withPermit {
        val requestId = UUID.randomUUID().toString().take(8)
        val elapsed = { System.currentTimeMillis() - startTime }

        repeat(maxRetries + 1) { attempt ->
            try {
                val body = client.fetchWithTimeout(url, timeoutMs)
                if (attempt > 0) {
                    logger.info { "[${requestId}] 请求 #${index + 1} 第${attempt + 1}次成功 (${elapsed()}ms)" }
                } else {
                    logger.info { "[${requestId}] 请求 #${index + 1} 完成 (${elapsed()}ms)" }
                }
                return@withPermit RequestResult(index, requestId, true, body, elapsed())
            } catch (e: Exception) {
                logger.warn { "[${requestId}] 请求 #${index + 1} 第${attempt + 1}次失败: ${e.message}" }
            }
        }

        logger.error { "[${requestId}] 请求 #${index + 1} ${maxRetries + 1}次全部失败 (${elapsed()}ms)" }
        RequestResult(index, requestId, false, "FAILED_${index + 1}", elapsed())
    }
}
