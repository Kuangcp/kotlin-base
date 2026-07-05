package learn.practice

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PracticeTest {

    // ==================== 1. 数据处理管道 ====================

    data class SalesRecord(
        val product: String,
        val category: String,
        val amount: Double,
        val quantity: Int
    )

    fun List<SalesRecord>.totalRevenue(): Double = sumOf { it.amount * it.quantity }

    fun List<SalesRecord>.topProducts(n: Int): List<Pair<String, Double>> {
        return groupBy { it.product }
            .map { (product, records) -> product to records.totalRevenue() }
            .sortedByDescending { it.second }
            .take(n)
    }

    fun List<SalesRecord>.byCategory(): Map<String, Double> {
        return groupBy { it.category }
            .mapValues { (_, records) -> records.totalRevenue() }
    }

    @Test
    fun `sales data pipeline`() {
        val sales = listOf(
            SalesRecord("iPhone", "Electronics", 999.0, 10),
            SalesRecord("MacBook", "Electronics", 1999.0, 5),
            SalesRecord("T-Shirt", "Clothing", 29.99, 50),
            SalesRecord("Jeans", "Clothing", 79.99, 30),
            SalesRecord("Coffee", "Food", 4.99, 200)
        )

        assertEquals(24882.2, sales.totalRevenue(), 0.01)

        val top2 = sales.topProducts(2)
        assertEquals("MacBook", top2[0].first) // 1999 * 5 = 9995
        assertEquals("iPhone", top2[1].first) // 999 * 10 = 9990

        val byCategory = sales.byCategory()
        assertEquals(24882.2, byCategory.values.sum(), 0.01)
    }

    // ==================== 2. 配置解析器 ====================

    sealed class ConfigValue {
        data class StringValue(val value: String) : ConfigValue()
        data class IntValue(val value: Int) : ConfigValue()
        data class BoolValue(val value: Boolean) : ConfigValue()
        data class ListValue(val value: List<String>) : ConfigValue()
    }

    data class Config(private val properties: Map<String, ConfigValue>) {
        fun getString(key: String): String? =
            (properties[key] as? ConfigValue.StringValue)?.value

        fun getInt(key: String): Int? =
            (properties[key] as? ConfigValue.IntValue)?.value

        fun getBool(key: String): Boolean? =
            (properties[key] as? ConfigValue.BoolValue)?.value

        fun getList(key: String): List<String>? =
            (properties[key] as? ConfigValue.ListValue)?.value

        fun getStringOrDefault(key: String, default: String): String =
            getString(key) ?: default
    }

    class ConfigParser {
        fun parse(input: String): Result<Config> {
            return try {
                val properties = mutableMapOf<String, ConfigValue>()
                input.lines().filter { it.isNotBlank() }.forEach { line ->
                    val parts = line.split("=", limit = 2)
                    if (parts.size == 2) {
                        val key = parts[0].trim()
                        val value = parseValue(parts[1].trim())
                        properties[key] = value
                    }
                }
                Result.success(Config(properties))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        private fun parseValue(raw: String): ConfigValue {
            return when {
                raw.equals("true", ignoreCase = true) -> ConfigValue.BoolValue(true)
                raw.equals("false", ignoreCase = true) -> ConfigValue.BoolValue(false)
                raw.toIntOrNull() != null -> ConfigValue.IntValue(raw.toInt())
                raw.startsWith("[") && raw.endsWith("]") -> {
                    val items = raw.removeSurrounding("[", "]")
                        .split(",")
                        .map { it.trim().removeSurrounding("\"") }
                    ConfigValue.ListValue(items)
                }
                else -> ConfigValue.StringValue(raw.removeSurrounding("\""))
            }
        }
    }

    @Test
    fun `config parser`() {
        val input = """
            name = "MyApp"
            version = 2
            debug = true
            features = ["auth", "api", "ui"]
        """.trimIndent()

        val config = ConfigParser().parse(input).getOrThrow()

        assertEquals("MyApp", config.getString("name"))
        assertEquals(2, config.getInt("version"))
        assertEquals(true, config.getBool("debug"))
        assertEquals(listOf("auth", "api", "ui"), config.getList("features"))
        assertEquals("default", config.getStringOrDefault("missing", "default"))
    }

    // ==================== 3. 简单计算器 ====================

    sealed class Expr {
        data class Num(val value: Double) : Expr()
        data class Add(val left: Expr, val right: Expr) : Expr()
        data class Sub(val left: Expr, val right: Expr) : Expr()
        data class Mul(val left: Expr, val right: Expr) : Expr()
        data class Div(val left: Expr, val right: Expr) : Expr()
    }

    fun eval(expr: Expr): Double = when (expr) {
        is Expr.Num -> expr.value
        is Expr.Add -> eval(expr.left) + eval(expr.right)
        is Expr.Sub -> eval(expr.left) - eval(expr.right)
        is Expr.Mul -> eval(expr.left) * eval(expr.right)
        is Expr.Div -> {
            val divisor = eval(expr.right)
            require(divisor != 0.0) { "Division by zero" }
            eval(expr.left) / divisor
        }
    }

    fun Expr.toInfix(): String = when (this) {
        is Expr.Num -> value.toString()
        is Expr.Add -> "(${left.toInfix()} + ${right.toInfix()})"
        is Expr.Sub -> "(${left.toInfix()} - ${right.toInfix()})"
        is Expr.Mul -> "(${left.toInfix()} * ${right.toInfix()})"
        is Expr.Div -> "(${left.toInfix()} / ${right.toInfix()})"
    }

    @Test
    fun `calculator`() {
        // (2 + 3) * 4 = 20
        val expr = Expr.Mul(
            Expr.Add(Expr.Num(2.0), Expr.Num(3.0)),
            Expr.Num(4.0)
        )
        assertEquals(20.0, eval(expr))
        assertEquals("((2.0 + 3.0) * 4.0)", expr.toInfix())
    }

    @Test
    fun `calculator complex`() {
        // (10 - 2) * (6 / 3) = 16
        val expr = Expr.Mul(
            Expr.Sub(Expr.Num(10.0), Expr.Num(2.0)),
            Expr.Div(Expr.Num(6.0), Expr.Num(3.0))
        )
        assertEquals(16.0, eval(expr))
    }

    // ==================== 4. 事件系统 ====================

    class EventBus {
        private val handlers = mutableMapOf<String, MutableList<(Any) -> Unit>>()

        fun <T : Any> on(event: String, handler: (T) -> Unit) {
            @Suppress("UNCHECKED_CAST")
            handlers.getOrPut(event) { mutableListOf() }
                .add(handler as (Any) -> Unit)
        }

        fun emit(event: String, data: Any) {
            handlers[event]?.forEach { it(data) }
        }

        fun handlerCount(event: String): Int = handlers[event]?.size ?: 0
    }

    @Test
    fun `event bus`() {
        val bus = EventBus()
        val received = mutableListOf<String>()

        bus.on<String>("message") { received.add(it) }
        bus.on<Int>("counter") { received.add("count:$it") }

        bus.emit("message", "hello")
        bus.emit("counter", 42)
        bus.emit("message", "world")

        assertEquals(listOf("hello", "count:42", "world"), received)
        assertEquals(1, bus.handlerCount("message"))
        assertEquals(1, bus.handlerCount("counter"))
    }

    // ==================== 5. LRU Cache ====================

    class LRUCache<K, V>(private val capacity: Int) {
        private val cache = LinkedHashMap<K, V>(capacity, 0.75f, true)

        fun get(key: K): V? = cache[key]

        fun put(key: K, value: V) {
            if (cache.size >= capacity) {
                val oldest = cache.keys.first()
                cache.remove(oldest)
            }
            cache[key] = value
        }

        fun size(): Int = cache.size
        fun contains(key: K): Boolean = cache.containsKey(key)
    }

    @Test
    fun `lru cache`() {
        val cache = LRUCache<String, Int>(3)

        cache.put("a", 1)
        cache.put("b", 2)
        cache.put("c", 3)

        assertEquals(1, cache.get("a"))
        cache.put("d", 4) // 淘汰 "b"

        assertEquals(null, cache.get("b"))
        assertEquals(4, cache.get("d"))
        assertEquals(3, cache.size())
    }

    // ==================== 6. Builder 模式 ====================

    data class Query private constructor(
        val table: String,
        val conditions: List<String>,
        val orderBy: String?,
        val limit: Int?
    ) {
        class Builder(private val table: String) {
            private val conditions = mutableListOf<String>()
            private var orderBy: String? = null
            private var limit: Int? = null

            fun where(condition: String) = apply { conditions.add(condition) }
            fun orderBy(column: String) = apply { orderBy = column }
            fun limit(n: Int) = apply { limit = n }

            fun build() = Query(table, conditions.toList(), orderBy, limit)
        }

        fun toSql(): String {
            val sb = StringBuilder("SELECT * FROM $table")
            if (conditions.isNotEmpty()) {
                sb.append(" WHERE ${conditions.joinToString(" AND ")}")
            }
            orderBy?.let { sb.append(" ORDER BY $it") }
            limit?.let { sb.append(" LIMIT $it") }
            return sb.toString()
        }
    }

    @Test
    fun `query builder`() {
        val query = Query.Builder("users")
            .where("age > 18")
            .where("active = true")
            .orderBy("name")
            .limit(10)
            .build()

        assertEquals(
            "SELECT * FROM users WHERE age > 18 AND active = true ORDER BY name LIMIT 10",
            query.toSql()
        )
    }
}
