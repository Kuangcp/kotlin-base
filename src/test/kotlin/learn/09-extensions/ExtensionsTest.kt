package learn.extensions

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExtensionsTest {

    // ==================== 扩展函数 ====================

    fun String.isEmail(): Boolean = contains("@") && contains(".")

    fun String.isPalindrome(): Boolean {
        val cleaned = lowercase().filter { it.isLetterOrDigit() }
        return cleaned == cleaned.reversed()
    }

    @Test
    fun `extension function on string`() {
        assertEquals(true, "test@example.com".isEmail())
        assertEquals(false, "not-an-email".isEmail())
    }

    @Test
    fun `palindrome check`() {
        assertEquals(true, "A man a plan a canal Panama".isPalindrome())
        assertEquals(true, "racecar".isPalindrome())
        assertEquals(false, "hello".isPalindrome())
    }

    // ==================== 扩展属性 ====================

    val String.lastChar: Char
        get() = this[length - 1]

    val String.firstChar: Char
        get() = first()

    @Test
    fun `extension property`() {
        assertEquals('n', "Kotlin".lastChar)
        assertEquals('K', "Kotlin".firstChar)
    }

    // ==================== 可空类型扩展 ====================

    fun String?.orDefault(default: String = "empty"): String = this ?: default

    @Test
    fun `nullable extension`() {
        val name: String? = null
        assertEquals("empty", name.orDefault())
        assertEquals("custom", name.orDefault("custom"))

        val validName: String? = "Kotlin"
        assertEquals("Kotlin", validName.orDefault())
    }

    // ==================== 泛型扩展函数 ====================

    fun <T> List<T>.secondOrNull(): T? = if (size >= 2) this[1] else null

    fun <T : Comparable<T>> List<T>.secondLargest(): T? {
        if (size < 2) return null
        val sorted = sorted()
        return sorted[sorted.size - 2]
    }

    @Test
    fun `generic extension function`() {
        assertEquals("b", listOf("a", "b", "c").secondOrNull())
        assertEquals(null, listOf("a").secondOrNull())
    }

    @Test
    fun `second largest`() {
        assertEquals(4, listOf(1, 2, 3, 4, 5).secondLargest())
        assertEquals(null, listOf(1).secondLargest())
    }

    // ==================== 中缀函数 ====================

    infix fun Int.power(n: Int): Int {
        var result = 1
        repeat(n) { result *= this }
        return result
    }

    infix fun String.repeatNTimes(n: Int): String = repeat(n)

    @Test
    fun `infix power`() {
        assertEquals(256, 2 power 8)
        assertEquals(1, 5 power 0)
        assertEquals(5, 5 power 1)
    }

    @Test
    fun `infix repeat`() {
        assertEquals("abcabcabc", "abc" repeatNTimes 3)
    }

    // ==================== 运算符重载 ====================

    data class Point(val x: Int, val y: Int) {
        operator fun plus(other: Point) = Point(x + other.x, y + other.y)
        operator fun minus(other: Point) = Point(x - other.x, y - other.y)
        operator fun times(n: Int) = Point(x * n, y * n)
        operator fun unaryMinus() = Point(-x, -y)
        operator fun get(index: Int) = when (index) {
            0 -> x
            1 -> y
            else -> throw IndexOutOfBoundsException()
        }
    }

    @Test
    fun `operator plus`() {
        assertEquals(Point(4, 6), Point(1, 2) + Point(3, 4))
    }

    @Test
    fun `operator minus`() {
        assertEquals(Point(-2, -2), Point(1, 2) - Point(3, 4))
    }

    @Test
    fun `operator times`() {
        assertEquals(Point(3, 6), Point(1, 2) * 3)
    }

    @Test
    fun `operator unary minus`() {
        assertEquals(Point(-1, -2), -Point(1, 2))
    }

    @Test
    fun `operator get`() {
        val point = Point(10, 20)
        assertEquals(10, point[0])
        assertEquals(20, point[1])
    }

    // ==================== 扩展的解析规则 ====================

    open class Base {
        fun baseMethod() = "Base"
    }

    class Derived : Base() {
        fun derivedMethod() = "Derived"
    }

    fun Base.extendedMethod() = "Extended Base"
    fun Derived.extendedMethod() = "Extended Derived"

    @Test
    fun `extension resolves statically`() {
        val base: Base = Derived()
        assertEquals("Extended Base", base.extendedMethod()) // 根据声明类型解析
    }

    // ==================== 实际应用 ====================

    fun Int.toDuration(): String {
        val hours = this / 3600
        val minutes = (this % 3600) / 60
        val seconds = this % 60
        return buildString {
            if (hours > 0) append("${hours}h ")
            if (minutes > 0) append("${minutes}m ")
            if (seconds > 0) append("${seconds}s")
        }.trim()
    }

    @Test
    fun `practical extension`() {
        assertEquals("1h 30m 45s", 5445.toDuration())
        assertEquals("30s", 30.toDuration())
        assertEquals("2h", 7200.toDuration())
    }

    fun <T> List<T>.chunkedAsString(size: Int, separator: String = ", "): String {
        return chunked(size).joinToString(separator) { it.joinToString(", ") }
    }

    @Test
    fun `chunked as string`() {
        val list = listOf(1, 2, 3, 4, 5, 6, 7)
        assertEquals("1, 2, 3 | 4, 5, 6 | 7", list.chunkedAsString(3, " | "))
    }
}
