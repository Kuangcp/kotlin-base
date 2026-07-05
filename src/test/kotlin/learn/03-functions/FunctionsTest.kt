package learn.functions

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class FunctionsTest {

    // ==================== 基本函数 ====================

    private fun add(a: Int, b: Int): Int {
        return a + b
    }

    @Test
    fun `basic function`() {
        assertEquals(5, add(2, 3))
        assertEquals(0, add(-1, 1))
    }

    // ==================== 单表达式函数 ====================

    private fun multiply(a: Int, b: Int) = a * b

    private fun isEven(n: Int) = n % 2 == 0

    @Test
    fun `single expression function`() {
        assertEquals(6, multiply(2, 3))
        assertEquals(0, multiply(0, 100))
    }

    @Test
    fun `single expression boolean`() {
        assertEquals(true, isEven(4))
        assertEquals(false, isEven(3))
    }

    // ==================== 默认参数 ====================

    private fun greet(name: String = "World") = "Hello, $name!"

    private fun createUser(name: String, age: Int = 0, active: Boolean = true): String {
        return "$name (age=$age, active=$active)"
    }

    @Test
    fun `default parameter`() {
        assertEquals("Hello, World!", greet())
        assertEquals("Hello, Kotlin!", greet("Kotlin"))
    }

    @Test
    fun `multiple default parameters`() {
        assertEquals("Alice (age=0, active=true)", createUser("Alice"))
        assertEquals("Bob (age=25, active=false)", createUser("Bob", age = 25, active = false))
    }

    // ==================== 命名参数 ====================

    @Test
    fun `named arguments`() {
        assertEquals("Bob (age=30, active=true)", createUser("Bob", active = true, age = 30))
    }

    // ==================== Unit 返回类型 ====================

    private fun printHello(): Unit {
        // Unit 类似 Java 的 void，可以省略
    }

    @Test
    fun `unit return type`() {
        // Unit 是单例，所有 Unit 类型相等
        assertEquals(Unit, printHello())
    }

    // ==================== Nothing 返回类型 ====================

    private fun fail(message: String): Nothing {
        throw IllegalArgumentException(message)
    }

    @Test
    fun `nothing return type`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            fail("error occurred")
        }
        assertEquals("error occurred", exception.message)
    }

    // ==================== vararg 可变参数 ====================

    private fun sum(vararg numbers: Int): Int = numbers.sum()

    private fun joinWords(vararg words: String, separator: String = ", "): String {
        return words.joinToString(separator)
    }

    @Test
    fun `vararg with numbers`() {
        assertEquals(6, sum(1, 2, 3))
        assertEquals(15, sum(1, 2, 3, 4, 5))
        assertEquals(0, sum())
    }

    @Test
    fun `vararg with named parameter`() {
        assertEquals("a, b, c", joinWords("a", "b", "c"))
        assertEquals("a | b | c", joinWords("a", "b", "c", separator = " | "))
    }

    // ==================== 函数类型 ====================

    private fun operate(a: Int, b: Int, op: (Int, Int) -> Int): Int {
        return op(a, b)
    }

    @Test
    fun `function as parameter`() {
        assertEquals(5, operate(2, 3) { a, b -> a + b })
        assertEquals(6, operate(2, 3) { a, b -> a * b })
    }

    @Test
    fun `lambda as variable`() {
        val addFunc: (Int, Int) -> Int = { a, b -> a + b }
        assertEquals(5, addFunc(2, 3))
    }

    // ==================== 函数引用 ====================

    private fun double(n: Int) = n * 2

    private fun isPositive(n: Int) = n > 0

    @Test
    fun `function reference`() {
        val numbers = listOf(1, 2, 3, 4, 5)

        val doubled = numbers.map(::double)
        assertEquals(listOf(2, 4, 6, 8, 10), doubled)

        val positives = numbers.filter(::isPositive)
        assertEquals(listOf(1, 2, 3, 4, 5), positives)
    }

    // ==================== 局部函数 ====================

    @Test
    fun `local function`() {
        fun factorial(n: Int): Int {
            fun innerFactorial(x: Int): Int {
                return if (x <= 1) 1 else x * innerFactorial(x - 1)
            }
            return innerFactorial(n)
        }

        assertEquals(1, factorial(0))
        assertEquals(1, factorial(1))
        assertEquals(120, factorial(5))
    }

    // ==================== 扩展函数预告 ====================

    private fun String.isEmail(): Boolean {
        return this.contains("@") && this.contains(".")
    }

    @Test
    fun `extension function preview`() {
        assertEquals(true, "test@example.com".isEmail())
        assertEquals(false, "not-an-email".isEmail())
    }

    // ==================== 匿名函数 ====================

    @Test
    fun `anonymous function`() {
        val add = fun(a: Int, b: Int): Int {
            return a + b
        }
        assertEquals(5, add(2, 3))
    }
}
