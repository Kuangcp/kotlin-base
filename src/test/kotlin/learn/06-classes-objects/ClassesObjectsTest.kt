package learn.classes

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class ClassesObjectsTest {

    // ==================== 基本类 ====================

    @Test
    fun `basic class with constructor`() {
        class Person(val name: String, var age: Int)

        val person = Person("Alice", 30)
        assertEquals("Alice", person.name)
        assertEquals(30, person.age)

        person.age = 31
        assertEquals(31, person.age)
    }

    @Test
    fun `class with init block`() {
        class Person(val name: String, var age: Int) {
            init {
                require(age >= 0) { "Age must be non-negative" }
            }

            fun greet() = "Hello, I'm $name ($age)"
        }

        val person = Person("Bob", 25)
        assertEquals("Hello, I'm Bob (25)", person.greet())
    }

    @Test
    fun `secondary constructor`() {
        class User(val name: String, val email: String) {
            var verified = false
                private set

            constructor(name: String) : this(name, "${name.lowercase()}@example.com") {
                verified = true
            }
        }

        val user1 = User("Alice", "alice@example.com")
        assertEquals(false, user1.verified)

        val user2 = User("Bob")
        assertEquals("bob@example.com", user2.email)
        assertEquals(true, user2.verified)
    }

    // ==================== data class ====================

    @Test
    fun `data class equals`() {
        data class User(val name: String, val age: Int)

        val user1 = User("Alice", 30)
        val user2 = User("Alice", 30)
        assertEquals(user1, user2)
    }

    @Test
    fun `data class toString`() {
        data class User(val name: String, val age: Int)
        val user = User("Alice", 30)
        assertEquals("User(name=Alice, age=30)", user.toString())
    }

    @Test
    fun `data class copy`() {
        data class User(val name: String, val age: Int, val email: String)

        val user = User("Alice", 30, "alice@example.com")
        val updated = user.copy(age = 31, email = "alice@new.com")

        assertEquals("Alice", updated.name)
        assertEquals(31, updated.age)
        assertEquals("alice@new.com", updated.email)
    }

    @Test
    fun `data class destructuring`() {
        data class User(val name: String, val age: Int)
        val user = User("Alice", 30)
        val (name, age) = user
        assertEquals("Alice", name)
        assertEquals(30, age)
    }

    // ==================== sealed class ====================

    @Test
    fun `sealed class when`() {
        sealed class Result {
            data class Success(val data: String) : Result()
            data class Error(val message: String) : Result()
            data object Loading : Result()
        }

        fun handleMessage(result: Result): String = when (result) {
            is Result.Success -> "Data: ${result.data}"
            is Result.Error -> "Error: ${result.message}"
            is Result.Loading -> "Loading..."
        }

        assertEquals("Data: hello", handleMessage(Result.Success("hello")))
        assertEquals("Error: not found", handleMessage(Result.Error("not found")))
        assertEquals("Loading...", handleMessage(Result.Loading))
    }

    @Test
    fun `sealed interface`() {
        sealed interface Shape {
            data class Circle(val radius: Double) : Shape
            data class Rectangle(val width: Double, val height: Double) : Shape
        }

        fun area(shape: Shape): Double = when (shape) {
            is Shape.Circle -> Math.PI * shape.radius * shape.radius
            is Shape.Rectangle -> shape.width * shape.height
        }

        assertEquals(Math.PI * 4, area(Shape.Circle(2.0)), 0.001)
        assertEquals(12.0, area(Shape.Rectangle(3.0, 4.0)), 0.001)
    }

    // ==================== enum class ====================

    @Test
    fun `enum class`() {
        enum class Color(val rgb: Int) {
            RED(0xFF0000),
            GREEN(0x00FF00),
            BLUE(0x0000FF);

            fun isRed() = this == RED
        }

        assertEquals(0xFF0000, Color.RED.rgb)
        assertEquals(true, Color.RED.isRed())
        assertEquals(3, Color.entries.size)
    }

    // ==================== object 单例 ====================

    @Test
    fun `object singleton`() {
        object Config {
            val version = "1.0"
            fun getInfo() = "Config v$version"
        }

        assertEquals("1.0", Config.version)
        assertEquals("Config v1.0", Config.getInfo())
    }

    // ==================== companion object ====================

    @Test
    fun `companion object factory`() {
        class User private constructor(val name: String, val email: String) {
            companion object {
                fun create(name: String) = User(name, "${name.lowercase()}@example.com")
                const val MAX_NAME_LENGTH = 50
            }
        }

        val user = User.create("Alice")
        assertEquals("Alice", user.name)
        assertEquals("alice@example.com", user.email)
        assertEquals(50, User.MAX_NAME_LENGTH)
    }

    // ==================== 嵌套类与内部类 ====================

    @Test
    fun `nested class`() {
        class Outer(val name: String) {
            class Nested(val value: String) {
                fun getValue() = value
            }
        }

        val nested = Outer.Nested("inner")
        assertEquals("inner", nested.getValue())
    }

    @Test
    fun `inner class`() {
        class Outer(val name: String) {
            inner class Inner {
                fun getOuterName() = name
            }
        }

        val outer = Outer("Outer")
        val inner = outer.Inner()
        assertEquals("Outer", inner.getOuterName())
    }

    // ==================== value class ====================

    @Test
    fun `value class`() {
        @JvmInline
        value class Meters(val value: Double) {
            fun toCentimeters() = value * 100
        }

        val distance = Meters(1.5)
        assertEquals(1.5, distance.value)
        assertEquals(150.0, distance.toCentimeters())
    }

    // ==================== 属性 getter/setter ====================

    @Test
    fun `custom getter`() {
        class Rectangle(val width: Double, val height: Double) {
            val area: Double
                get() = width * height
        }

        val rect = Rectangle(3.0, 4.0)
        assertEquals(12.0, rect.area)
    }

    @Test
    fun `backing field`() {
        class Counter {
            var count = 0
                set(value) {
                    if (value >= 0) field = value
                }
        }

        val counter = Counter()
        counter.count = 5
        assertEquals(5, counter.count)

        counter.count = -1
        assertEquals(5, counter.count) // 负值被拒绝
    }
}
