package learn.lambdas

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LambdasHigherOrderTest {

    // ==================== Lambda 基础 ====================

    @Test
    fun `basic lambda`() {
        val add = { a: Int, b: Int -> a + b }
        assertEquals(5, add(2, 3))
    }

    @Test
    fun `single parameter it`() {
        val double = { it: Int -> it * 2 }
        assertEquals(6, double(3))
    }

    @Test
    fun `lambda as parameter`() {
        fun apply(x: Int, f: (Int) -> Int) = f(x)
        assertEquals(10, apply(5) { it * 2 })
    }

    // ==================== 高阶函数 ====================

    @Test
    fun `function as parameter`() {
        fun operate(a: Int, b: Int, op: (Int, Int) -> Int) = op(a, b)
        assertEquals(5, operate(2, 3) { a, b -> a + b })
        assertEquals(6, operate(2, 3) { a, b -> a * b })
    }

    @Test
    fun `function as return type`() {
        fun multiplier(factor: Int): (Int) -> Int = { it * factor }
        val triple = multiplier(3)
        assertEquals(15, triple(5))
    }

    @Test
    fun `function reference`() {
        fun double(n: Int) = n * 2
        val numbers = listOf(1, 2, 3)
        val doubled = numbers.map(::double)
        assertEquals(listOf(2, 4, 6), doubled)
    }

    // ==================== 作用域函数 ====================

    @Test
    fun `let transform`() {
        val result = "Hello, Kotlin!".let {
            it.length
        }
        assertEquals(14, result)
    }

    @Test
    fun `let with nullable`() {
        val name: String? = "Kotlin"
        val length = name?.let { it.length } ?: 0
        assertEquals(6, length)

        val nullName: String? = null
        val nullLength = nullName?.let { it.length } ?: 0
        assertEquals(0, nullLength)
    }

    @Test
    fun `run in context`() {
        data class Person(var name: String, var age: Int)
        val greeting = Person("Alice", 30).run {
            "Hello, $name! You are $age years old."
        }
        assertEquals("Hello, Alice! You are 30 years old.", greeting)
    }

    @Test
    fun `with non-extension`() {
        data class Person(val name: String, val age: Int)
        val result = with(Person("Bob", 25)) {
            "Name: $name, Age: $age"
        }
        assertEquals("Name: Bob, Age: 25", result)
    }

    @Test
    fun `apply configure`() {
        data class Person(var name: String, var age: Int, var city: String)
        val person = Person("", 0, "").apply {
            name = "Alice"
            age = 30
            city = "Beijing"
        }
        assertEquals("Alice", person.name)
        assertEquals(30, person.age)
        assertEquals("Beijing", person.city)
    }

    @Test
    fun `also side effect`() {
        val numbers = mutableListOf(1, 2, 3)
        val result = numbers.also { it.add(4) }
        assertEquals(listOf(1, 2, 3, 4), numbers)
        assertEquals(listOf(1, 2, 3, 4), result) // 返回同一对象
    }

    // ==================== 作用域函数对比 ====================

    @Test
    fun `scope functions comparison`() {
        data class Person(var name: String, var age: Int)

        // let: 转换，返回 lambda 结果
        val letResult = Person("Alice", 30).let { it.name.uppercase() }
        assertEquals("ALICE", letResult)

        // run: 在上下文中执行，返回 lambda 结果
        val runResult = Person("Bob", 25).run { "$name ($age)" }
        assertEquals("Bob (25)", runResult)

        // with: 同 run，但不是扩展函数
        val withResult = with(Person("Charlie", 35)) { "$name ($age)" }
        assertEquals("Charlie (35)", withResult)

        // apply: 配置对象，返回对象本身
        val applyResult = Person("", 0).apply { name = "Dave"; age = 40 }
        assertEquals("Dave", applyResult.name)

        // also: 附加操作，返回对象本身
        val alsoResult = mutableListOf(1, 2).also { it.add(3) }
        assertEquals(listOf(1, 2, 3), alsoResult)
    }

    // ==================== inline ====================

    inline fun measureTime(block: () -> Unit) {
        val start = System.nanoTime()
        block()
        val end = System.nanoTime()
        assertTrue(end >= start)
    }

    @Test
    fun `inline function`() {
        measureTime { Thread.sleep(1) }
    }

    // ==================== 匿名函数 ====================

    @Test
    fun `anonymous function`() {
        val add = fun(a: Int, b: Int): Int {
            return a + b
        }
        assertEquals(5, add(2, 3))
    }

    // ==================== it 和多参数 ====================

    @Test
    fun `lambda with multiple params`() {
        val sum = { a: Int, b: Int -> a + b }
        assertEquals(7, sum(3, 4))
    }

    @Test
    fun `destructuring in lambda`() {
        data class Entry(val key: String, val value: Int)
        val map = listOf(Entry("a", 1), Entry("b", 2))
        val result = map.map { (k, v) -> "$k=$v" }
        assertEquals(listOf("a=1", "b=2"), result)
    }

    // ==================== 实际应用 ====================

    @Test
    fun `builder pattern`() {
        val html = StringBuilder().apply {
            append("<html>")
            append("<body>")
            append("<h1>Hello</h1>")
            append("</body>")
            append("</html>")
        }.toString()

        assertEquals("<html><body><h1>Hello</h1></body></html>", html)
    }

    @Test
    fun `filter and transform`() {
        val result = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
            .filter { it % 2 == 0 }
            .map { it * it }
            .fold(0) { acc, i -> acc + i }
        assertEquals(220, result) // 4+16+36+64+100
    }
}
