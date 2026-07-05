package learn.coroutines

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.*
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CoroutinesTest {

    // ==================== 基础 ====================

    @Test
    fun `runBlocking basics`() = runTest {
        var result = ""
        launch {
            delay(10)
            result = "done"
        }
        delay(20)
        assertEquals("done", result)
    }

    // ==================== suspend ====================

    private suspend fun fetchData(): String {
        delay(10)
        return "data"
    }

    @Test
    fun `suspend function`() = runTest {
        val data = fetchData()
        assertEquals("data", data)
    }

    // ==================== launch ====================

    @Test
    fun `launch fire and forget`() = runTest {
        var counter = 0
        val jobs = List(10) {
            launch {
                delay(1)
                counter++
            }
        }
        jobs.forEach { it.join() }
        assertEquals(10, counter)
    }

    // ==================== async/await ====================

    @Test
    fun `async await`() = runTest {
        val deferred1 = async {
            delay(10)
            10
        }
        val deferred2 = async {
            delay(10)
            20
        }
        val sum = deferred1.await() + deferred2.await()
        assertEquals(30, sum)
    }

    // ==================== Dispatcher ====================

    @Test
    fun `withContext`() = runTest {
        val result = withContext(Dispatchers.Default) {
            "computed on ${Thread.currentThread().name}"
        }
        assertTrue(result.startsWith("computed on"))
    }

    // ==================== CoroutineScope ====================

    @Test
    fun `coroutine scope`() = runTest {
        val results = mutableListOf<String>()

        coroutineScope {
            launch { results.add("a") }
            launch { results.add("b") }
            launch { results.add("c") }
        }

        assertEquals(3, results.size)
        assertTrue(results.containsAll(listOf("a", "b", "c")))
    }

    // ==================== exception handling ====================

    @Test
    fun `exception handling with supervisor scope`() = kotlinx.coroutines.runBlocking {
        var caughtException: Exception? = null

        val job = supervisorScope {
            launch {
                try {
                    throw RuntimeException("test error")
                } catch (e: Exception) {
                    caughtException = e
                }
            }
        }
        job.join()

        assertEquals("test error", caughtException?.message)
    }

    // ==================== Job ====================

    @Test
    fun `job cancel`() = runTest {
        val job = launch {
            repeat(1000) { i ->
                delay(10)
                yield()
            }
        }
        delay(5)
        job.cancel()
        assertTrue(job.isCancelled)
    }

    // ==================== Flow 基础 ====================

    private fun numbers(): Flow<Int> = flow {
        for (i in 1..5) {
            delay(1)
            emit(i)
        }
    }

    @Test
    fun `flow collect`() = runTest {
        val collected = mutableListOf<Int>()
        numbers().collect { collected.add(it) }
        assertEquals(listOf(1, 2, 3, 4, 5), collected)
    }

    // ==================== Flow 操作符 ====================

    @Test
    fun `flow map filter`() = runTest {
        val result = numbers()
            .filter { it % 2 == 0 }
            .map { it * it }
            .toList()
        assertEquals(listOf(4, 16), result)
    }

    @Test
    fun `flow reduce`() = runTest {
        val sum = numbers().reduce { acc, i -> acc + i }
        assertEquals(15, sum)
    }

    @Test
    fun `flow fold`() = runTest {
        val result = numbers().fold(mutableListOf<Int>()) { acc, i ->
            acc.apply { add(i * 10) }
        }
        assertEquals(listOf(10, 20, 30, 40, 50), result)
    }

    // ==================== Flow 组合 ====================

    @Test
    fun `flow zip`() = runTest {
        val names = flowOf("Alice", "Bob", "Charlie")
        val ages = flowOf(25, 30, 35)

        val result = names.zip(ages) { name, age -> "$name: $age" }.toList()
        assertEquals(listOf("Alice: 25", "Bob: 30", "Charlie: 35"), result)
    }

    @Test
    fun `flow combine`() = runTest {
        val flow1 = flowOf(1, 2, 3)
        val flow2 = flowOf("a", "b", "c")

        val result = flow1.combine(flow2) { num, str -> "$num$str" }.toList()
        assertEquals(listOf("1a", "2b", "3c"), result)
    }

    // ==================== Flow 转换 ====================

    @Test
    fun `flow flatMapConcat`() = runTest {
        val result = flowOf(1, 2, 3)
            .flatMapConcat { value ->
                flow {
                    emit(value)
                    emit(value * 10)
                }
            }
            .toList()
        assertEquals(listOf(1, 10, 2, 20, 3, 30), result)
    }

    @Test
    fun `flow take`() = runTest {
        val result = numbers().take(3).toList()
        assertEquals(listOf(1, 2, 3), result)
    }

    // ==================== StateFlow ====================

    @Test
    fun `state flow`() = runTest {
        val state = MutableStateFlow(0)
        val values = mutableListOf<Int>()

        val job = launch {
            state.collect { values.add(it) }
        }

        delay(10)
        state.value = 1
        delay(10)
        state.value = 2
        delay(10)
        state.value = 3
        delay(20)
        job.cancel()

        assertTrue(values.contains(0))
        assertTrue(values.contains(3))
    }

    // ==================== Channel ====================

    @Test
    fun `channel basic`() = runTest {
        val channel = Channel<Int>(Channel.BUFFERED)

        launch {
            for (i in 1..5) {
                channel.send(i)
            }
            channel.close()
        }

        val received = mutableListOf<Int>()
        for (value in channel) {
            received.add(value)
        }
        assertEquals(listOf(1, 2, 3, 4, 5), received)
    }

    // ==================== 并发控制 ====================

    @Test
    fun `mutex`() = runTest {
        val mutex = Mutex()
        var counter = 0

        val jobs = List(100) {
            launch {
                mutex.withLock {
                    counter++
                }
            }
        }
        jobs.forEach { it.join() }
        assertEquals(100, counter)
    }

    // ==================== asyncAll ====================

    @Test
    fun `async all`() = runTest {
        val urls = listOf("url1", "url2", "url3")

        val results = urls.map { url ->
            async {
                delay(10)
                "result from $url"
            }
        }.awaitAll()

        assertEquals(3, results.size)
        assertTrue(results.all { it.startsWith("result from") })
    }

    // ==================== flowOn ====================

    @Test
    fun `flow on`() = runTest {
        val result = flow {
            emit("data")
        }
            .flowOn(Dispatchers.IO)
            .toList()
        assertEquals(listOf("data"), result)
    }

    // ==================== 异常处理 ====================

    @Test
    fun `flow catch`() = runTest {
        val result = flow {
            emit(1)
            emit(2)
            throw RuntimeException("error")
        }
            .catch { emit(-1) }
            .toList()
        assertEquals(listOf(1, 2, -1), result)
    }

    @Test
    fun `flow onCompletion`() = runTest {
        var completed = false
        numbers()
            .onCompletion { completed = true }
            .collect()
        assertTrue(completed)
    }
}
