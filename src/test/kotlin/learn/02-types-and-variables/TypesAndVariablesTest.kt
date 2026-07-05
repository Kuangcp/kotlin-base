package learn.types

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TypesAndVariablesTest {

    // ==================== val vs var ====================

    @Test
    fun `val is immutable`() {
        val immutable = 42
        assertEquals(42, immutable)
        // immutable = 43  // 编译错误：Val cannot be reassigned
    }

    @Test
    fun `var is mutable`() {
        var mutable = 42
        assertEquals(42, mutable)
        mutable = 43
        assertEquals(43, mutable)
    }

    // ==================== 基本类型 ====================

    @Test
    fun `int types`() {
        val intVal: Int = 42
        val longVal: Long = 42L
        val shortVal: Short = 42
        val byteVal: Byte = 42

        assertEquals(42, intVal)
        assertEquals(42L, longVal)
        assertEquals(42.toShort(), shortVal)
        assertEquals(42.toByte(), byteVal)
    }

    @Test
    fun `floating point types`() {
        val doubleVal: Double = 3.14
        val floatVal: Float = 3.14f

        assertEquals(3.14, doubleVal, 0.001)
        assertEquals(3.14f, floatVal, 0.001f)
    }

    @Test
    fun `boolean and char`() {
        val boolVal: Boolean = true
        val charVal: Char = 'A'

        assertTrue(boolVal)
        assertEquals('A', charVal)
    }

    @Test
    fun `string type`() {
        val str: String = "Hello"
        assertEquals("Hello", str)
        assertEquals(5, str.length)
    }

    // ==================== 类型推断 ====================

    @Test
    fun `type inference with int`() {
        val a = 42       // 推断为 Int
        val b = 42L      // 推断为 Long
        assertEquals(Int::class, a::class)
        assertEquals(Long::class, b::class)
    }

    @Test
    fun `type inference with string`() {
        val name = "Kotlin"  // 推断为 String
        assertEquals(String::class, name::class)
    }

    @Test
    fun `explicit type declaration`() {
        val a: Long = 42    // 显式声明为 Long
        val b: Double = 42.0  // Double 字面量
        assertEquals(42L, a)
        assertEquals(42.0, b, 0.001)
    }

    // ==================== 字符串模板 ====================

    @Test
    fun `simple string template`() {
        val name = "Kotlin"
        val result = "Hello, $name"
        assertEquals("Hello, Kotlin", result)
    }

    @Test
    fun `expression in string template`() {
        val a = 10
        val b = 20
        val result = "Sum: ${a + b}"
        assertEquals("Sum: 30", result)
    }

    @Test
    fun `multi-line raw string`() {
        val result = """
            Name: Kotlin
            Version: 2.1
        """.trimIndent()
        assertEquals("Name: Kotlin\nVersion: 2.1", result)
    }

    @Test
    fun `raw string preserves formatting`() {
        val result = """
            Line 1
                Indented
            Line 3
        """.trimIndent()
        assertEquals("Line 1\n    Indented\nLine 3", result)
    }

    // ==================== 类型转换 ====================

    @Test
    fun `string to number`() {
        assertEquals(42, "42".toInt())
        assertEquals(3.14, "3.14".toDouble(), 0.001)
        assertEquals(42L, "42".toLong())
    }

    @Test
    fun `number to string`() {
        assertEquals("42", 42.toString())
        assertEquals("3.14", 3.14.toString())
    }

    @Test
    fun `number type conversion`() {
        val intVal = 42
        assertEquals(42L, intVal.toLong())
        assertEquals(42.0, intVal.toDouble(), 0.001)
        assertEquals(42.toFloat(), intVal.toFloat(), 0.001f)
    }

    // ==================== 数组 ====================

    @Test
    fun `int array`() {
        val arr = intArrayOf(1, 2, 3, 4, 5)
        assertEquals(5, arr.size)
        assertEquals(1, arr[0])
        assertEquals(15, arr.sum())
    }

    @Test
    fun `string array`() {
        val arr = arrayOf("Hello", "Kotlin", "World")
        assertEquals(3, arr.size)
        assertEquals("Kotlin", arr[1])
    }

    // ==================== 常量 ====================

    @Test
    fun `const val`() {
        // const val 只能在顶层或 object 中声明
        // 编译期常量，替换为字面量
        assertEquals(MAX_SIZE, 100)
        assertEquals(APP_NAME, "KotlinLearn")
    }

    companion object {
        const val MAX_SIZE = 100
        const val APP_NAME = "KotlinLearn"
    }
}
