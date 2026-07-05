# 13-testing 测试

## 知识点

- JUnit5 基础：`@Test`、`@BeforeEach`、`@AfterEach`
- 断言：`assertEquals`、`assertTrue`、`assertThrows`
- 参数化测试 `@ParameterizedTest`、`@MethodSource`
- 测试命名规范与组织
- 嵌套测试 `@Nested`
- 生命周期回调
- 条件执行 `@EnabledOnOs`、`@Disabled`

## JUnit5 基础

```kotlin
class CalculatorTest {
    private lateinit var calculator: Calculator

    @BeforeEach
    fun setup() {
        calculator = Calculator()
    }

    @Test
    fun `add two numbers`() {
        assertEquals(5, calculator.add(2, 3))
    }
}
```

## 参数化测试

```kotlin
@ParameterizedTest
@MethodSource("addProvider")
fun `parameterized add`(a: Int, b: Int, expected: Int) {
    assertEquals(expected, calculator.add(a, b))
}

companion object {
    @JvmStatic
    fun addProvider() = Stream.of(
        Arguments.of(1, 2, 3),
        Arguments.of(0, 0, 0),
        Arguments.of(-1, 1, 0)
    )
}
```

## 运行

```bash
gradle test --tests "learn.testing.*"
```
