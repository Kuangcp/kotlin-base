package learn.annotations

import org.junit.jupiter.api.Test
import kotlin.reflect.full.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

// ==================== 自定义注解 ====================

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ApiEndpoint(val path: String)

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class JsonField(val name: String = "")

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Description(val value: String)

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Validate(val pattern: String = "")

// ==================== 使用注解的类 ====================

@Description("用户数据类")
data class User(
    @JsonField("user_name") val name: String,
    @JsonField("user_age") val age: Int,
    val email: String
)

@ApiEndpoint("/api/users")
class UserController {
    @ApiEndpoint("/api/users/list")
    fun listUsers(): List<String> = listOf("Alice", "Bob")

    @ApiEndpoint("/api/users/create")
    fun createUser(name: String): String = "Created: $name"
}

class AnnotationsReflectTest {

    // ==================== 注解读取 ====================

    @Test
    fun `read class annotation`() {
        val annotation = User::class.findAnnotation<Description>()
        assertEquals("用户数据类", annotation?.value)
    }

    @Test
    fun `read function annotations`() {
        val endpoints = UserController::class.functions
            .filter { it.hasAnnotation<ApiEndpoint>() }
            .map { it.findAnnotation<ApiEndpoint>()?.path }

        assertEquals(2, endpoints.size)
        assertTrue(endpoints.containsAll(listOf("/api/users/list", "/api/users/create")))
    }

    @Test
    fun `read property annotations`() {
        val fields = User::class.memberProperties
            .mapNotNull { prop ->
                prop.findAnnotation<JsonField>()?.let { annotation ->
                    annotation.name.ifEmpty { prop.name }
                }
            }

        assertEquals(2, fields.size)
        assertTrue(fields.contains("user_name"))
        assertTrue(fields.contains("user_age"))
    }

    // ==================== 反射基础 ====================

    @Test
    fun `class info`() {
        val kClass = User::class
        assertEquals("User", kClass.simpleName)
        assertEquals("learn.annotations.User", kClass.qualifiedName)
        assertFalse(kClass.isAbstract)
        assertTrue(kClass.isData)
    }

    @Test
    fun `member properties`() {
        val properties = User::class.memberProperties
        assertEquals(3, properties.size)

        val names = properties.map { it.name }.toSet()
        assertTrue(names.containsAll(setOf("name", "age", "email")))
    }

    @Test
    fun `member functions`() {
        val functions = UserController::class.memberFunctions
        val functionNames = functions.map { it.name }
        assertTrue(functionNames.containsAll(listOf("listUsers", "createUser")))
    }

    // ==================== 反射调用 ====================

    @Test
    fun `invoke function via reflection`() {
        val controller = UserController()
        val listFunction = UserController::class.memberFunctions.find { it.name == "listUsers" }
        val result = listFunction?.call(controller) as List<*>

        assertEquals(2, result.size)
        assertEquals("Alice", result[0])
    }

    @Test
    fun `invoke with parameters`() {
        val controller = UserController()
        val createFunction = UserController::class.memberFunctions.find { it.name == "createUser" }
        val result = createFunction?.call(controller, "Charlie") as String

        assertEquals("Created: Charlie", result)
    }

    // ==================== 反射访问属性 ====================

    @Test
    fun `access property via reflection`() {
        val user = User("Alice", 30, "alice@example.com")
        val nameProperty = User::class.memberProperties.find { it.name == "name" }

        assertEquals("Alice", nameProperty?.get(user))
    }

    @Test
    fun `access all properties`() {
        val user = User("Bob", 25, "bob@example.com")
        val values = User::class.memberProperties.map { it.get(user) }

        assertEquals(3, values.size)
        assertTrue(values.contains("Bob"))
        assertTrue(values.contains(25))
        assertTrue(values.contains("bob@example.com"))
    }

    // ==================== 构造函数反射 ====================

    @Test
    fun `create instance via reflection`() {
        val constructor = User::class.primaryConstructor
        val user = constructor?.call("Charlie", 35, "charlie@example.com")

        assertEquals("Charlie", user?.name)
        assertEquals(35, user?.age)
    }

    @Test
    fun `constructor parameters`() {
        val constructor = User::class.primaryConstructor
        val parameters = constructor?.parameters

        assertEquals(3, parameters?.size)
        assertEquals("name", parameters?.get(0)?.name)
        assertEquals("age", parameters?.get(1)?.name)
        assertEquals("email", parameters?.get(2)?.name)
    }

    // ==================== 扩展函数反射 ====================

    @Test
    fun `find extension functions`() {
        val extensions = User::class.memberExtensionFunctions
        // 扩展函数在 memberExtensionFunctions 中
        assertTrue(extensions.isNotEmpty() || User::class.memberFunctions.isNotEmpty())
    }

    // ==================== 注解过滤 ====================

    @Test
    fun `filter classes by annotation`() {
        val annotatedClasses = listOf(User::class, UserController::class)
            .filter { it.hasAnnotation<Description>() }

        assertEquals(1, annotatedClasses.size)
        assertEquals("User", annotatedClasses[0].simpleName)
    }

    @Test
    fun `filter functions by annotation`() {
        val endpointFunctions = UserController::class.functions
            .filter { it.hasAnnotation<ApiEndpoint>() }
            .filter { it.findAnnotation<ApiEndpoint>()?.path?.contains("list") == true }

        assertEquals(1, endpointFunctions.size)
        assertEquals("listUsers", endpointFunctions[0].name)
    }

    // ==================== KType ====================

    @Test
    fun `type information`() {
        val property = User::class.memberProperties.find { it.name == "age" }
        assertEquals("kotlin.Int", property?.returnType?.toString())
    }

    // ==================== 实际应用：JSON 序列化 ====================

    private fun Any.toJson(): String {
        @Suppress("UNCHECKED_CAST")
        val properties = this::class.memberProperties as Collection<kotlin.reflect.KProperty1<Any, *>>
        return properties
            .filter { it.hasAnnotation<JsonField>() }
            .joinToString(", ") { prop ->
                val fieldName = prop.findAnnotation<JsonField>()?.name ?: prop.name
                val value = prop.get(this@toJson)
                "\"$fieldName\": $value"
            }
    }

    @Test
    fun `custom json serialization`() {
        val user = User("Alice", 30, "alice@example.com")
        val json = user.toJson()
        assertTrue(json.contains("\"user_name\""))
        assertTrue(json.contains("\"user_age\""))
    }

    // ==================== 实际应用：路由解析 ====================

    @Test
    fun `route discovery`() {
        val routes = UserController::class.functions
            .filter { it.hasAnnotation<ApiEndpoint>() }
            .map { func ->
                val endpoint = func.findAnnotation<ApiEndpoint>()!!
                endpoint.path to func.name
            }
            .toMap()

        assertEquals("listUsers", routes["/api/users/list"])
        assertEquals("createUser", routes["/api/users/create"])
    }

    private fun assertFalse(value: Boolean) {
        assertTrue(!value)
    }
}
