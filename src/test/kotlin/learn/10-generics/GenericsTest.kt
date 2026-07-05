package learn.generics

import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private val logger = KotlinLogging.logger {}

class GenericsTest {

    // ==================== 泛型类 ====================

    class Box<T>(val value: T) {
        fun <R> map(transform: (T) -> R): Box<R> = Box(transform(value))
    }

    @Test
    fun `generic class`() {
        val intBox = Box(42)
        val strBox = Box("hello")

        assertEquals(42, intBox.value)
        assertEquals("hello", strBox.value)
    }

    @Test
    fun `box with transform`() {
        val box = Box(5)
        val stringBox = box.map { "Number: $it" }
        assertEquals("Number: 5", stringBox.value)
    }

    // ==================== 泛型函数 ====================

    fun <T> singletonList(item: T): List<T> = listOf(item)

    fun <T> List<T>.secondOrNull(): T? = if (size >= 2) this[1] else null

    fun <T : Comparable<T>> List<T>.maxOrNull(): T? {
        if (isEmpty()) return null
        var max = this[0]
        for (item in this) {
            if (item > max) max = item
        }
        return max
    }

    @Test
    fun `generic function`() {
        assertEquals(listOf("hello"), singletonList("hello"))
        assertEquals(listOf(42), singletonList(42))
    }

    @Test
    fun `second or null`() {
        assertEquals("b", listOf("a", "b").secondOrNull())
        assertEquals(null, listOf("a").secondOrNull())
    }

    @Test
    fun `max or null`() {
        assertEquals(5, listOf(1, 3, 5, 2, 4).maxOrNull())
        assertEquals("c", listOf("a", "c", "b").maxOrNull())
    }

    // ==================== 类型约束 ====================

    fun <T> List<T>.toFormattedString(separator: String = ", "): String
        where T : CharSequence, T : Comparable<T> {
        return sorted().joinToString(separator)
    }

    @Test
    fun `type constraint`() {
        val result = listOf("banana", "apple", "cherry").toFormattedString()
        assertEquals("apple, banana, cherry", result)
    }

    // ==================== 型变 out (协变) ====================

    interface Source<out T> {
        fun next(): T
    }

    class IntSource(private val value: Int) : Source<Int> {
        override fun next() = value
    }

    @Test
    fun `covariance out`() {
        val intSource: Source<Int> = IntSource(42)
        // Source<Int> 可以赋值给 Source<Any>（协变）
        val anySource: Source<Any> = intSource
        assertEquals(42, anySource.next())
    }

    // ==================== 型变 in (逆变) ====================

    interface Consumer<in T> {
        fun consume(item: T)
    }

    class AnyConsumer : Consumer<Any> {
        val items = mutableListOf<Any>()
        override fun consume(item: Any) {
            items.add(item)
        }
    }

    @Test
    fun `contravariance in`() {
        val anyConsumer = AnyConsumer()
        // Consumer<Any> 可以赋值给 Consumer<String>（逆变）
        val stringConsumer: Consumer<String> = anyConsumer
        stringConsumer.consume("hello")
        assertEquals("hello", anyConsumer.items[0])
    }

    // ==================== 星投影 ====================

    fun printAll(list: List<*>) {
        for (item in list) {
            assertTrue(item is Any?)
        }
    }

    @Test
    fun `star projection`() {
        printAll(listOf(1, "hello", 3.14))
        printAll(emptyList<Int>())
    }

    // ==================== reified ====================

    inline fun <reified T> Any.isType(): Boolean = this is T

    inline fun <reified T> List<Any>.filterByType(): List<T> = filterIsInstance<T>()

    inline fun <reified T : Enum<T>> enumEntries(): Array<T> = T::class.java.enumConstants

    @Test
    fun `reified type check`() {
        assertTrue(42.isType<Int>())
        assertTrue("hello".isType<String>())
        assertTrue(!42.isType<String>())
    }

    @Test
    fun `reified filter`() {
        val mixed = listOf(1, "hello", 3.14, "world", 42)
        val strings = mixed.filterByType<String>()
        assertEquals(listOf("hello", "world"), strings)

        val ints = mixed.filterByType<Int>()
        assertEquals(listOf(1, 42), ints)
    }

    // ==================== 多类型参数 ====================

    class Pair<A, B>(val first: A, val second: B) {
        fun swap(): Pair<B, A> = Pair(second, first)
        fun <C> mapFirst(transform: (A) -> C): Pair<C, B> = Pair(transform(first), second)
        fun <C> mapSecond(transform: (B) -> C): Pair<A, C> = Pair(first, transform(second))
    }

    @Test
    fun `multiple type parameters`() {
        val pair = Pair(1, "hello")
        val swapped = pair.swap()
        assertEquals("hello", swapped.first)
        assertEquals(1, swapped.second)

        val mapped = pair.mapFirst { it * 2 }
        assertEquals(2, mapped.first)
        assertEquals("hello", mapped.second)
    }

    // ==================== 泛型与 where ====================

    fun <T> findMax(list: List<T>): T where T : Comparable<T>, T : Any {
        require(list.isNotEmpty()) { "List must not be empty" }
        var max = list[0]
        for (item in list) {
            if (item > max) max = item
        }
        return max
    }

    @Test
    fun `where clause`() {
        assertEquals(5, findMax(listOf(1, 3, 5, 2, 4)))
        assertEquals("c", findMax(listOf("a", "c", "b")))
    }

    // ==================== 实际应用 ====================

    class ResultHolder<T> private constructor(private val value: T?, private val error: String?) {
        val isSuccess: Boolean get() = error == null
        val data: T get() = value ?: throw IllegalStateException("No data: $error")

        companion object {
            fun <T> success(data: T) = ResultHolder(data, null)
            fun <T> failure(error: String) = ResultHolder<T>(null, error)
        }

        fun <R> map(transform: (T) -> R): ResultHolder<R> =
            if (isSuccess) ResultHolder.success(transform(data))
            else ResultHolder.failure(error!!)
    }

    @Test
    fun `result holder`() {
        val success = ResultHolder.success(42)
        assertTrue(success.isSuccess)
        assertEquals(42, success.data)

        val doubled = success.map { it * 2 }
        assertEquals(84, doubled.data)

        val failure = ResultHolder.failure<Int>("error occurred")
        assertTrue(!failure.isSuccess)
    }
}
