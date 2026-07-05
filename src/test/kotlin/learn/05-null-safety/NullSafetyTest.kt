package learn.nullsafety

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class NullSafetyTest {

    // ==================== 可空类型 ====================

    @Test
    fun `non-nullable cannot be null`() {
        val name: String = "Kotlin"
        assertEquals("Kotlin", name)
        // name = null  // 编译错误
    }

    @Test
    fun `nullable can be null`() {
        val name: String? = null
        assertNull(name)
    }

    // ==================== 安全调用 ?. ====================

    @Test
    fun `safe call on non-null`() {
        val name: String? = "Kotlin"
        val length = name?.length
        assertEquals(6, length)
    }

    @Test
    fun `safe call on null`() {
        val name: String? = null
        val length = name?.length
        assertNull(length)
    }

    @Test
    fun `chained safe calls`() {
        data class Address(val city: String?)
        data class User(val address: Address?)

        val user: User? = User(Address("Beijing"))
        val city = user?.address?.city
        assertEquals("Beijing", city)

        val userNull: User? = null
        assertNull(userNull?.address?.city)
    }

    // ==================== Elvis 操作符 ?: ====================

    @Test
    fun `elvis operator with null`() {
        val name: String? = null
        val length = name?.length ?: 0
        assertEquals(0, length)
    }

    @Test
    fun `elvis operator with non-null`() {
        val name: String? = "Kotlin"
        val length = name?.length ?: 0
        assertEquals(6, length)
    }

    @Test
    fun `elvis with default`() {
        val config: Map<String, String>? = null
        val host = config?.get("host") ?: "localhost"
        assertEquals("localhost", host)
    }

    // ==================== 非空断言 !! ====================

    @Test
    fun `non-null assertion success`() {
        val name: String? = "Kotlin"
        val length = name!!.length
        assertEquals(6, length)
    }

    @Test
    fun `non-null assertion throws`() {
        val name: String? = null
        assertFailsWith<KotlinNullPointerException> {
            name!!.length
        }
    }

    // ==================== let 处理可空值 ====================

    @Test
    fun `let with non-null`() {
        val name: String? = "Kotlin"
        var result = ""
        name?.let {
            result = it.uppercase()
        }
        assertEquals("KOTLIN", result)
    }

    @Test
    fun `let with null`() {
        val name: String? = null
        var result = "default"
        name?.let {
            result = it.uppercase()
        }
        assertEquals("default", result)
    }

    @Test
    fun `let for filtering`() {
        val names = listOf("Alice", null, "Bob", null, "Charlie")
        val validNames = names.mapNotNull { it?.uppercase() }
        assertEquals(listOf("ALICE", "BOB", "CHARLIE"), validNames)
    }

    // ==================== 安全类型转换 as? ====================

    @Test
    fun `safe cast success`() {
        val obj: Any = "Hello"
        val str = obj as? String
        assertEquals("Hello", str)
    }

    @Test
    fun `safe cast failure`() {
        val obj: Any = 42
        val str = obj as? String
        assertNull(str)
    }

    // ==================== is 类型检查与智能转换 ====================

    @Test
    fun `type check with is`() {
        val obj: Any = "Kotlin"
        if (obj is String) {
            // 智能转换：obj 在 this 作用域内自动转为 String
            assertEquals(6, obj.length)
        }
    }

    @Test
    fun `type check with !is`() {
        val obj: Any = 42
        if (obj !is String) {
            // obj 在 this 作用域内仍为 Any
            assertEquals(42, obj)
        }
    }

    @Test
    fun `smart cast in when`() {
        fun process(obj: Any): String = when (obj) {
            is String -> "String: ${obj.uppercase()}"
            is Int -> "Int: ${obj * 2}"
            is List<*> -> "List size: ${obj.size}"
            else -> "Unknown"
        }

        assertEquals("String: KOTLIN", process("kotlin"))
        assertEquals("Int: 84", process(42))
        assertEquals("List size: 3", process(listOf(1, 2, 3)))
    }

    @Test
    fun `smart cast with &&`() {
        val obj: Any = "Hello"
        if (obj is String && obj.length > 3) {
            // obj 智能转换为 String
            assertEquals(5, obj.length)
        }
    }

    // ==================== 前置条件 ====================

    @Test
    fun `require passes`() {
        val age = 25
        require(age >= 0) { "Age must be non-negative" }
        assertEquals(25, age)
    }

    @Test
    fun `require throws`() {
        assertFailsWith<IllegalArgumentException> {
            require(-1 >= 0) { "Age must be non-negative" }
        }
    }

    @Test
    fun `check passes`() {
        val name = "Kotlin"
        check(name.isNotEmpty()) { "Name must not be empty" }
        assertEquals("Kotlin", name)
    }

    @Test
    fun `check throws`() {
        assertFailsWith<IllegalStateException> {
            check(false) { "Check failed" }
        }
    }

    // ==================== let 链式处理 ====================

    @Test
    fun `let chaining`() {
        val result = "  Hello, Kotlin!  "
            .trim()
            .let { trimmed ->
                if (trimmed.isNotEmpty()) trimmed.uppercase() else "EMPTY"
            }
        assertEquals("HELLO, KOTLIN!", result)
    }

    @Test
    fun `let with nullable chain`() {
        data class User(val name: String?, val email: String?)

        val user = User("Alice", "alice@example.com")
        val info = user.name?.let { name ->
            user.email?.let { email ->
                "$name <$email>"
            }
        }
        assertEquals("Alice <alice@example.com>", info)
    }
}
