package learn.delegation

import kotlinx.coroutines.*
import org.junit.jupiter.api.Test
import kotlin.properties.Delegates.observable
import kotlin.properties.Delegates.vetoable
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.test.assertEquals

class DelegationTest {

    // ==================== by lazy ====================

    @Test
    fun `by lazy`() {
        var computeCount = 0
        val expensiveValue by lazy {
            computeCount++
            42
        }

        assertEquals(0, computeCount)
        assertEquals(42, expensiveValue)
        assertEquals(1, computeCount)
        assertEquals(42, expensiveValue) // 不会重新计算
        assertEquals(1, computeCount)
    }

    // ==================== by lazy thread safe ====================

    @Test
    fun `by lazy thread safety`() = kotlinx.coroutines.runBlocking {
        var counter = 0
        val lazyValue by lazy {
            counter++
            "computed"
        }

        val jobs = List(10) {
            launch {
                lazyValue // 多线程访问，只会计算一次
            }
        }
        jobs.forEach { it.join() }
        assertEquals(1, counter)
    }

    // ==================== by observable ====================

    @Test
    fun `by observable`() {
        var changes = mutableListOf<String>()
        var name: String by observable("初始") { _, old, new ->
            changes.add("$old -> $new")
        }

        name = "Alice"
        name = "Bob"

        assertEquals(listOf("初始 -> Alice", "Alice -> Bob"), changes)
    }

    // ==================== by vetoable ====================

    @Test
    fun `by vetoable`() {
        var age: Int by vetoable(0) { _, _, newValue ->
            newValue >= 0 // 只允许非负值
        }

        age = 25
        assertEquals(25, age)

        age = -1 // 被拒绝
        assertEquals(25, age) // 保持原值
    }

    // ==================== by map ====================

    @Test
    fun `by map`() {
        val map = mutableMapOf("name" to "Alice", "age" to "30")
        val name: String by map
        val age: String by map

        assertEquals("Alice", name)
        assertEquals("30", age)
    }

    // ==================== 自定义委托 ReadOnlyProperty ====================

    class ConstantDelegate(private val value: Int) : ReadOnlyProperty<Any?, Int> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): Int = value
    }

    @Test
    fun `custom read only delegate`() {
        val answer by ConstantDelegate(42)
        assertEquals(42, answer)
    }

    // ==================== 自定义委托 ReadWriteProperty ====================

    class ValidatedInt(private var field: Int = 0) : ReadWriteProperty<Any?, Int> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): Int = field
        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
            require(value in 0..100) { "Value must be 0-100" }
            field = value
        }
    }

    @Test
    fun `custom read write delegate`() {
        var score by ValidatedInt()
        score = 85
        assertEquals(85, score)

        // 超出范围会被拒绝
        try {
            score = 150
        } catch (e: IllegalArgumentException) {
            assertEquals("Value must be 0-100", e.message)
        }
        assertEquals(85, score) // 保持原值
    }

    // ==================== 委托提供者 ====================

    class DelegateProvider {
        operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): ReadOnlyProperty<Any?, String> {
            return object : ReadOnlyProperty<Any?, String> {
                override fun getValue(thisRef: Any?, property: KProperty<*>): String {
                    return "Provided for ${property.name}"
                }
            }
        }
    }

    @Test
    fun `delegate provider`() {
        val value by DelegateProvider()
        assertEquals("Provided for value", value)
    }

    // ==================== 类委托 ====================

    interface Printer {
        fun print(message: String): String
    }

    class SimplePrinter : Printer {
        override fun print(message: String): String = "Printed: $message"
    }

    class LoggingPrinter(private val delegate: Printer) : Printer by delegate {
        private val logs = mutableListOf<String>()

        override fun print(message: String): String {
            logs.add(message)
            return delegate.print(message)
        }

        fun getLogs(): List<String> = logs.toList()
    }

    @Test
    fun `class delegation`() {
        val printer = LoggingPrinter(SimplePrinter())
        val result = printer.print("hello")
        assertEquals("Printed: hello", result)
        assertEquals(listOf("hello"), printer.getLogs())
    }

    // ==================== 类委托 with extra methods ====================

    interface Repository {
        fun findAll(): List<String>
        fun findById(id: Int): String?
    }

    class InMemoryRepository : Repository {
        private val data = mapOf(1 to "Alice", 2 to "Bob", 3 to "Charlie")

        override fun findAll(): List<String> = data.values.toList()
        override fun findById(id: Int): String? = data[id]
    }

    class CachedRepository(private val delegate: Repository) : Repository by delegate {
        private val cache = mutableMapOf<Int, String>()

        override fun findById(id: Int): String? {
            return cache.getOrPut(id) {
                delegate.findById(id) ?: return null
            }
        }

        fun cacheSize(): Int = cache.size
    }

    @Test
    fun `class delegation with cache`() {
        val repo = CachedRepository(InMemoryRepository())

        assertEquals("Alice", repo.findById(1))
        assertEquals(1, repo.cacheSize())

        repo.findById(1) // 从缓存获取
        assertEquals(1, repo.cacheSize())

        assertEquals(listOf("Alice", "Bob", "Charlie"), repo.findAll())
    }
}
