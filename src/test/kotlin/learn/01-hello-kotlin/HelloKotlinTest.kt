package learn.hello

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class HelloKotlinTest {

    @Test
    fun `hello world`() {
        val result = "Hello, Kotlin!"
        assertEquals("Hello, Kotlin!", result)
    }

    @Test
    fun `string template with variable`() {
        val name = "Kotlin"
        val result = "Language: $name"
        assertEquals("Language: Kotlin", result)
    }

    @Test
    fun `string template with expression`() {
        val a = 3
        val b = 4
        val result = "$a + $b = ${a + b}"
        assertEquals("3 + 4 = 7", result)
    }

    @Test
    fun `multi-line string`() {
        val result = """
            Line 1
            Line 2
            Line 3
        """.trimIndent()
        assertEquals("Line 1\nLine 2\nLine 3", result)
    }

    @Test
    fun `string template with method call`() {
        val name = "kotlin"
        val result = "Uppercase: ${name.uppercase()}"
        assertEquals("Uppercase: KOTLIN", result)
    }

    @Test
    fun `basic main output`() {
        // Simulating what main() would print
        val output = buildString {
            appendLine("Hello, Kotlin!")
            appendLine("Language: Kotlin, Version: 2.1")
            appendLine("1 + 1 = ${1 + 1}")
        }
        assertEquals(
            """
                Hello, Kotlin!
                Language: Kotlin, Version: 2.1
                1 + 1 = 2
            """.trimIndent(),
            output.trimEnd()
        )
    }
}
