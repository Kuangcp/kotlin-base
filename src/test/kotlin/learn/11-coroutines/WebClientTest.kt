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

    // client 是单例，不配置全局超时，超时由每次请求决定
    private val client = HttpClient(CIO)

    @Test
    fun `async http get`() = runBlocking {
        val totalTimeoutMs = 8000L     // 总超时 8秒
        val perRequestTimeoutMs = 2000L // 单次超时 2秒
        val concurrency = 2             // 最大并发数
        val totalRequests = 8           // 总请求数
        val url = "https://httpbin.org/get"

        val semaphore = Semaphore(concurrency)

        logger.info { "开始 $totalRequests 次 HTTP 请求，并发=$concurrency，单次超时=${perRequestTimeoutMs}ms，总超时=${totalTimeoutMs}ms" }

        val results = mutableListOf<String>()
        val startTime = System.currentTimeMillis()

        val totalTime = measureTimeMillis {
            try {
                withTimeout(totalTimeoutMs.milliseconds) {
                    coroutineScope {
                        val jobs = List(totalRequests) { index ->
                            async {
                                semaphore.withPermit {
                                    val requestId = UUID.randomUUID().toString().take(8)
                                    try {
                                        // 每次请求单独设置超时
                                        withTimeout(perRequestTimeoutMs.milliseconds) {
                                            val response = client.get(url) {
                                                timeout {
                                                    requestTimeoutMillis = perRequestTimeoutMs
                                                    connectTimeoutMillis = perRequestTimeoutMs
                                                    socketTimeoutMillis = perRequestTimeoutMs
                                                }
                                            }
                                            val body = response.bodyAsText()
                                            val elapsed = System.currentTimeMillis() - startTime
                                            logger.info { "[${requestId}] 请求 #${index + 1} 完成 (${elapsed}ms)，状态码=${response.status}" }
                                            body
                                        }
                                    } catch (e: TimeoutCancellationException) {
                                        val elapsed = System.currentTimeMillis() - startTime
                                        logger.warn { "[${requestId}] 请求 #${index + 1} 超时 (${elapsed}ms)" }
                                        "TIMEOUT_${index + 1}"
                                    }
                                }
                            }
                        }
                        jobs.forEach { results.add(it.await()) }
                    }
                }
            } catch (e: TimeoutCancellationException) {
                logger.error { "总超时 ${totalTimeoutMs}ms 已到，取消剩余请求" }
            }
        }

        logger.info { "全部完成，总耗时=${totalTime}ms" }
        logger.info { "结果列表 (${results.size} 项):" }
        results.forEachIndexed { index, result ->
            logger.info { "  [$index] ${result.take(100)}..." }
        }

        assertTrue(totalTime < totalTimeoutMs + 2000, "总耗时 ${totalTime}ms 超过预期")
        assertTrue(results.isNotEmpty(), "应该有请求结果")

        logger.info { "测试通过！共完成 ${results.size} 个请求" }

        client.close()
    }
}
