package learn.collections

import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private val logger = KotlinLogging.logger {}

class CollectionsTest {

    // ==================== 不可变集合 ====================

    @Test
    fun `immutable list`() {
        val list = listOf(1, 2, 3, 4, 5)
        assertEquals(5, list.size)
        assertEquals(1, list[0])
        assertEquals(5, list[4])
    }

    @Test
    fun `immutable set`() {
        val set = setOf(1, 2, 2, 3, 3)
        assertEquals(3, set.size) // 去重
        assertTrue(set.contains(2))
    }

    @Test
    fun `immutable map`() {
        val map = mapOf("a" to 1, "b" to 2, "c" to 3)
        assertEquals(3, map.size)
        assertEquals(2, map["b"])
        assertEquals(null, map["d"])
    }

    // ==================== 可变集合 ====================

    @Test
    fun `mutable list`() {
        val list = mutableListOf(1, 2, 3)
        list.add(4)
        list.removeAt(0)
        assertEquals(listOf(2, 3, 4), list)
    }

    @Test
    fun `mutable set`() {
        val set = mutableSetOf(1, 2, 3)
        set.add(2) // 不会重复添加
        assertEquals(3, set.size)
    }

    @Test
    fun `mutable map`() {
        val map = mutableMapOf("a" to 1)
        map["b"] = 2
        map.remove("a")
        assertEquals(mapOf("b" to 2), map)
    }

    // ==================== 集合操作 ====================

    @Test
    fun `filter`() {
        val even = listOf(1, 2, 3, 4, 5, 6).filter { it % 2 == 0 }
        assertEquals(listOf(2, 4, 6), even)
    }

    @Test
    fun `map`() {
        val squared = listOf(1, 2, 3).map { it * it }
        assertEquals(listOf(1, 4, 9), squared)
    }

    @Test
    fun `flatMap`() {
        val nested = listOf(listOf(1, 2), listOf(3, 4), listOf(5))
        val flat = nested.flatMap { it }
        assertEquals(listOf(1, 2, 3, 4, 5), flat)
    }

    @Test
    fun `reduce`() {
        val sum = listOf(1, 2, 3, 4, 5).reduce { acc, i -> acc + i }
        assertEquals(15, sum)
    }

    @Test
    fun `fold`() {
        val result = listOf(1, 2, 3, 4, 5).fold(100) { acc, i -> acc + i }
        assertEquals(115, result)
    }

    @Test
    fun `chained operations`() {
        val result = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
            .filter { it % 2 == 0 }
            .map { it * it }
            .fold(0) { acc, i -> acc + i }
        assertEquals(220, result) // 4+16+36+64+100
    }

    // ==================== 查找 ====================

    @Test
    fun `any all none`() {
        val list = listOf(1, 2, 3, 4, 5)
        assertTrue(list.any { it > 4 })
        assertTrue(list.all { it > 0 })
        assertTrue(list.none { it > 10 })
    }

    @Test
    fun `find and firstOrNull`() {
        val list = listOf(1, 2, 3, 4, 5)
        assertEquals(4, list.find { it > 3 })
        assertEquals(null, list.firstOrNull { it > 10 })
    }

    @Test
    fun `count`() {
        val list = listOf(1, 2, 3, 4, 5)
        assertEquals(3, list.count { it > 2 })
    }

    // ==================== 分组 ====================

    @Test
    fun `groupBy`() {
        val words = listOf("apple", "banana", "avocado", "blueberry", "cherry")
        val grouped = words.groupBy { it.first() }
        assertEquals(listOf("apple", "avocado"), grouped['a'])
        assertEquals(listOf("banana", "blueberry"), grouped['b'])
    }

    @Test
    fun `partition`() {
        val (evens, odds) = listOf(1, 2, 3, 4, 5, 6).partition { it % 2 == 0 }
        assertEquals(listOf(2, 4, 6), evens)
        assertEquals(listOf(1, 3, 5), odds)
    }

    // ==================== 组合 ====================

    @Test
    fun `zip`() {
        val names = listOf("Alice", "Bob", "Charlie")
        val ages = listOf(25, 30, 35)
        val combined = names.zip(ages)
        assertEquals(listOf("Alice" to 25, "Bob" to 30, "Charlie" to 35), combined)
    }

    @Test
    fun `zip with transform`() {
        val names = listOf("Alice", "Bob")
        val ages = listOf(25, 30)
        val result = names.zip(ages) { name, age -> "$name: $age" }
        assertEquals(listOf("Alice: 25", "Bob: 30"), result)
    }

    // ==================== 分块与窗口 ====================

    @Test
    fun `chunked`() {
        val list = listOf(1, 2, 3, 4, 5, 6, 7)
        val chunks = list.chunked(3)
        assertEquals(listOf(listOf(1, 2, 3), listOf(4, 5, 6), listOf(7)), chunks)
    }

    @Test
    fun `windowed`() {
        val list = listOf(1, 2, 3, 4, 5)
        val windows = list.windowed(3)
        assertEquals(listOf(listOf(1, 2, 3), listOf(2, 3, 4), listOf(3, 4, 5)), windows)
    }

    // ==================== 转换 ====================

    @Test
    fun `associate`() {
        val list = listOf("Alice", "Bob", "Charlie")
        val map = list.associate { it.length to it }
        assertEquals(mapOf(5 to "Alice", 3 to "Bob", 7 to "Charlie"), map)
    }

    @Test
    fun `toMutable conversion`() {
        val immutable = listOf(1, 2, 3)
        val mutable = immutable.toMutableList()
        mutable.add(4)
        assertEquals(listOf(1, 2, 3, 4), mutable)
        assertEquals(3, immutable.size) // 原集合不变
    }

    // ==================== 排序 ====================

    @Test
    fun `sorted`() {
        val list = listOf(3, 1, 4, 1, 5, 9, 2, 6)
        assertEquals(listOf(1, 1, 2, 3, 4, 5, 6, 9), list.sorted())
        assertEquals(listOf(9, 6, 5, 4, 3, 2, 1, 1), list.sortedDescending())
    }

    @Test
    fun `sortedBy`() {
        data class Person(val name: String, val age: Int)
        val people = listOf(Person("Alice", 30), Person("Bob", 25), Person("Charlie", 35))
        val byAge = people.sortedBy { it.age }
        assertEquals("Bob", byAge[0].name)
        assertEquals("Charlie", byAge[2].name)
    }

    // ==================== Sequence ====================

    @Test
    fun `sequence lazy evaluation`() {
        val result = (1..1_000_000).asSequence()
            .filter { it % 2 == 0 }
            .take(5)
            .toList()
        assertEquals(listOf(2, 4, 6, 8, 10), result)
    }

    @Test
    fun `sequence vs eager`() {
        // Eager: 每步创建中间集合
        val eager = listOf(1, 2, 3, 4, 5)
            .filter { logger.debug { "filter $it" }; it > 2 }
            .map { logger.debug { "map $it" }; it * 2 }
        logger.debug { "--- eager ---" }

        // Lazy: 逐个处理
        val lazy = listOf(1, 2, 3, 4, 5).asSequence()
            .filter { logger.debug { "filter $it" }; it > 2 }
            .map { logger.debug { "map $it" }; it * 2 }
            .toList()
        logger.debug { "--- lazy ---" }

        assertEquals(eager, lazy)
    }

    // ==================== 解构遍历 ====================

    @Test
    fun `destructure map`() {
        val map = mapOf("a" to 1, "b" to 2, "c" to 3)
        val result = mutableListOf<String>()
        for ((key, value) in map) {
            result.add("$key=$value")
        }
        assertEquals(3, result.size)
    }

    // ==================== expand & zipWithNext ====================

    @Test
    fun `zipWithNext`() {
        val list = listOf(1, 2, 3, 4, 5)
        val pairs = list.zipWithNext()
        assertEquals(listOf(1 to 2, 2 to 3, 3 to 4, 4 to 5), pairs)
    }

    @Test
    fun `flatten`() {
        val nested = listOf(listOf(1, 2), listOf(3, 4), listOf(5))
        val flat = nested.flatten()
        assertEquals(listOf(1, 2, 3, 4, 5), flat)
    }

    @Test
    fun `distinct`() {
        val list = listOf(1, 2, 2, 3, 3, 3, 4)
        assertEquals(listOf(1, 2, 3, 4), list.distinct())
    }
}
