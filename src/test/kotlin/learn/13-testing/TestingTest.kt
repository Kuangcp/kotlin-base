package learn.testing

import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.function.Executable
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.Arguments
import java.util.stream.Stream

private val logger = KotlinLogging.logger {}

class TestingTest {

    // ==================== 基础 ====================

    private var counter = 0

    @BeforeEach
    fun setup() {
        counter = 0
    }

    @AfterEach
    fun teardown() {
        // 清理工作
    }

    @Test
    fun `basic test`() {
        assertEquals(4, 2 + 2)
        assertTrue(5 > 3)
        assertFalse(2 > 3)
    }

    @Test
    fun `string assertions`() {
        val name = "Kotlin"
        assertEquals(6, name.length)
        assertTrue(name.startsWith("K"))
        assertTrue(name.endsWith("n"))
        assertTrue(name.contains("ot"))
    }

    // ==================== 异常测试 ====================

    @Test
    fun `exception thrown`() {
        val exception = assertThrows<IllegalArgumentException> {
            require(false) { "Error occurred" }
        }
        assertEquals("Error occurred", exception.message)
    }

    @Test
    fun `multiple assertions`() {
        assertAll(
            { assertEquals(4, 2 + 2) },
            { assertTrue(5 > 3) },
            { assertEquals("hello".uppercase(), "HELLO") }
        )
    }

    // ==================== 嵌套测试 ====================

    @Nested
    inner class CalculatorTests {
        private fun add(a: Int, b: Int) = a + b
        private fun divide(a: Int, b: Int): Int {
            if (b == 0) throw ArithmeticException("Division by zero")
            return a / b
        }

        @Test
        fun `add positive numbers`() {
            assertEquals(5, add(2, 3))
        }

        @Test
        fun `add negative numbers`() {
            assertEquals(-5, add(-2, -3))
        }

        @Test
        fun `divide normal`() {
            assertEquals(5, divide(10, 2))
        }

        @Test
        fun `divide by zero throws`() {
            assertThrows<ArithmeticException> { divide(10, 0) }
        }
    }

    // ==================== 参数化测试 ====================

    @ParameterizedTest
    @CsvSource("1,2,3", "0,0,0", "-1,1,0", "100,200,300")
    fun `parameterized add`(a: Int, b: Int, expected: Int) {
        assertEquals(expected, a + b)
    }

    @ParameterizedTest
    @MethodSource("stringProvider")
    fun `parameterized string`(input: String, expectedLength: Int) {
        assertEquals(expectedLength, input.length)
    }

    companion object {
        @JvmStatic
        fun stringProvider(): Stream<Arguments> = Stream.of(
            Arguments.of("hello", 5),
            Arguments.of("", 0),
            Arguments.of("Kotlin", 6)
        )
    }

    // ==================== 测试生命周期 ====================

    @Test
    fun `test order`() {
        counter++
        assertEquals(1, counter)
    }

    @Test
    @Order(2)
    fun `test with display name`() {
        // @DisplayName 可以在测试报告中显示更友好的名字
        assertEquals(4, 2 * 2)
    }

    // ==================== 条件执行 ====================

    @Test
    @Disabled("Not yet implemented")
    fun `pending test`() {
        fail<Any>("This should be skipped")
    }

    // ==================== Timeout ====================

    @Test
    @Timeout(5)
    fun `test with timeout`() {
        // 如果超过 5 秒则失败
        assertEquals(1, 1)
    }

    // ==================== assertLinesMatch ====================

    @Test
    fun `lines match`() {
        val expected = listOf("Line 1", "Line 2", "Line 3")
        val actual = listOf("Line 1", "Line 2", "Line 3")
        assertLinesMatch(expected, actual)
    }

    // ==================== map assertions ====================

    @Test
    fun `map assertions`() {
        val map = mapOf("a" to 1, "b" to 2, "c" to 3)
        assertEquals(3, map.size)
        assertEquals(2, map["b"])
        assertTrue(map.containsKey("a"))
        assertTrue(map.containsValue(3))
    }

    // ==================== 集合断言 ====================

    @Test
    fun `collection assertions`() {
        val list = listOf(1, 2, 3, 4, 5)
        assertIterableEquals(
            listOf(2, 4),
            list.filter { it % 2 == 0 }
        )
    }

    // ==================== AssertJ 风格 (用标准库) ====================

    @Test
    fun `assert all properties`() {
        data class User(val name: String, val age: Int, val email: String)
        val user = User("Alice", 30, "alice@example.com")

        assertAll(
            { assertEquals("Alice", user.name) },
            { assertEquals(30, user.age) },
            { assertTrue(user.email.contains("@")) }
        )
    }
}
