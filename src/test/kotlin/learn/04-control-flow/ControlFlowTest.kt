package learn.controlflow

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

class ControlFlowTest {

    // ==================== if 表达式 ====================

    @Test
    fun `if as expression`() {
        val a = 10
        val b = 20
        val max = if (a > b) a else b
        assertEquals(20, max)
    }

    @Test
    fun `if with block`() {
        val x = 5
        val result = if (x > 0) {
            "positive"
        } else if (x == 0) {
            "zero"
        } else {
            "negative"
        }
        assertEquals("positive", result)
    }

    // ==================== when 表达式 ====================

    @Test
    fun `when with value`() {
        val score = 85
        val grade = when (score) {
            in 90..100 -> "A"
            in 80 until 90 -> "B"
            in 70 until 80 -> "C"
            in 60 until 70 -> "D"
            else -> "F"
        }
        assertEquals("B", grade)
    }

    @Test
    fun `when with multiple values`() {
        val day = "Saturday"
        val type = when (day) {
            "Monday", "Tuesday", "Wednesday", "Thursday", "Friday" -> "Weekday"
            "Saturday", "Sunday" -> "Weekend"
            else -> "Unknown"
        }
        assertEquals("Weekend", type)
    }

    @Test
    fun `when without argument`() {
        val score = 92
        val grade = when {
            score >= 90 -> "A"
            score >= 80 -> "B"
            score >= 70 -> "C"
            score >= 60 -> "D"
            else -> "F"
        }
        assertEquals("A", grade)
    }

    @Test
    fun `when with type check`() {
        fun describe(obj: Any): String = when (obj) {
            is Int -> "Integer: ${obj + 1}"
            is String -> "String length: ${obj.length}"
            is Boolean -> if (obj) "true" else "false"
            else -> "Unknown"
        }

        assertEquals("Integer: 43", describe(42))
        assertEquals("String length: 6", describe("Kotlin"))
        assertEquals("true", describe(true))
        assertEquals("Unknown", describe(3.14))
    }

    @Test
    fun `when with in`() {
        val char = 'K'
        val result = when (char) {
            in 'A'..'Z' -> "Uppercase"
            in 'a'..'z' -> "Lowercase"
            in '0'..'9' -> "Digit"
            else -> "Other"
        }
        assertEquals("Uppercase", result)
    }

    // ==================== for 循环与区间 ====================

    @Test
    fun `range inclusive`() {
        val list = mutableListOf<Int>()
        for (i in 1..5) list.add(i)
        assertEquals(listOf(1, 2, 3, 4, 5), list)
    }

    @Test
    fun `range until exclusive`() {
        val list = mutableListOf<Int>()
        for (i in 1 until 5) list.add(i)
        assertEquals(listOf(1, 2, 3, 4), list)
    }

    @Test
    fun `downTo`() {
        val list = mutableListOf<Int>()
        for (i in 5 downTo 1) list.add(i)
        assertEquals(listOf(5, 4, 3, 2, 1), list)
    }

    @Test
    fun `step`() {
        val list = mutableListOf<Int>()
        for (i in 1..10 step 2) list.add(i)
        assertEquals(listOf(1, 3, 5, 7, 9), list)
    }

    @Test
    fun `for with index`() {
        val fruits = listOf("apple", "banana", "cherry")
        val result = mutableListOf<String>()
        for ((index, fruit) in fruits.withIndex()) {
            result.add("$index: $fruit")
        }
        assertEquals(listOf("0: apple", "1: banana", "2: cherry"), result)
    }

    // ==================== while 与 do-while ====================

    @Test
    fun `while loop`() {
        var sum = 0
        var i = 1
        while (i <= 10) {
            sum += i
            i++
        }
        assertEquals(55, sum)
    }

    @Test
    fun `do-while loop`() {
        var count = 0
        do {
            count++
        } while (count < 5)
        assertEquals(5, count)
    }

    // ==================== break/continue 与标签 ====================

    @Test
    fun `break with label`() {
        outer@ for (i in 1..5) {
            for (j in 1..5) {
                if (j == 3) break@outer
            }
        }
        // break@outer 跳出外层循环
        assertEquals(true, true) // 编译通过即可
    }

    @Test
    fun `continue with label`() {
        val list = mutableListOf<Int>()
        outer@ for (i in 1..5) {
            for (j in 1..5) {
                if (j == 3) continue@outer
                list.add(i * 10 + j)
            }
        }
        // 每次 j==3 时跳过外层循环的剩余部分
        assertEquals(listOf(11, 12, 21, 22, 31, 32, 41, 42, 51, 52), list)
    }

    // ==================== 解构声明 ====================

    @Test
    fun `destructuring declaration`() {
        data class Point(val x: Int, val y: Int)
        val point = Point(10, 20)
        val (x, y) = point
        assertEquals(10, x)
        assertEquals(20, y)
    }

    @Test
    fun `destructuring in loop`() {
        val map = mapOf("a" to 1, "b" to 2, "c" to 3)
        val result = mutableListOf<String>()
        for ((key, value) in map) {
            result.add("$key=$value")
        }
        assertEquals(3, result.size)
    }

    @Test
    fun `destructuring function return`() {
        fun minMax(numbers: List<Int>): Pair<Int, Int> {
            return Pair(numbers.min(), numbers.max())
        }
        val (min, max) = minMax(listOf(3, 1, 4, 1, 5, 9))
        assertEquals(1, min)
        assertEquals(9, max)
    }
}
