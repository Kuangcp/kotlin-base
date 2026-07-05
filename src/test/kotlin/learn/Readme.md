# Kotlin 系统学习路径

循序渐进的 Kotlin 学习大纲，每个模块包含知识点说明和可运行的单元测试示例。

## 学习路线

```
基础语法 (01-04) → 空安全与类 (05-06) → 集合与函数式 (07-08)
→ 进阶特性 (09-12) → 实战应用 (13-15)
```

## 模块总览

| #  | 模块 | 主题 | 前置依赖 |
|----|------|------|----------|
| 01 | hello-kotlin | 环境搭建、基本语法、REPL、String 模板 | -- |
| 02 | types-and-variables | val/var、基本类型、类型推断、类型转换 | 01 |
| 03 | functions | 函数定义、默认参数、命名参数、单表达式函数 | 02 |
| 04 | control-flow | if/when/for/while、区间、when 表达式 | 03 |
| 05 | null-safety | 可空类型、安全调用、!!、let、类型检查 is/as | 02 |
| 06 | classes-objects | 类、构造函数、data class、sealed class、object、伴生对象 | 03 |
| 07 | collections | List/Set/Map、可变与不可变、集合操作链、序列 | 03 |
| 08 | lambdas-higher-order | Lambda、高阶函数、作用域函数、SAM 转换 | 07 |
| 09 | extensions | 扩展函数/属性、中缀函数、运算符重载 | 06 |
| 10 | generics | 泛型、型变 in/out、reified、类型约束 | 06 |
| 11 | coroutines | 协程基础、suspend、Flow、Channel、结构化并发 | 08 |
| 12 | delegation | 属性委托、类委托、by lazy、by observable | 06 |
| 13 | testing | JUnit5、Kotest、Mockk、测试策略 | 08 |
| 14 | annotations-reflect | 注解定义、反射、KSP | 10 |
| 15 | practice | 综合项目：CLI 工具 / Web API | 01-14 |

## 详细大纲

### 01-hello-kotlin 环境搭建

- Kotlin/JVM 环境配置（sdkman）
- Kotlin REPL 使用
- Hello World 程序
- String 模板 `$variable`、`${expression}`
- `main` 函数与 `println`
- `readln()` 读取输入

### 02-types-and-variables 类型与变量

- `val`（不可变）vs `var`（可变）
- 基本类型：`Int`、`Long`、`Double`、`Float`、`Boolean`、`Char`、`String`
- 类型推断与显式声明
- 字符串模板与原始字符串 `"""..."""`
- 类型转换：`toInt()`、`toDouble()`、`toString()`
- 数组类型：`IntArray`、`Array<String>`
- 常量 `const val` 与顶层常量

### 03-functions 函数

- 函数定义语法 `fun name(param: Type): ReturnType`
- 单表达式函数 `fun add(a: Int, b: Int) = a + b`
- 默认参数与命名参数
- `Unit` 与 `Nothing` 返回类型
- 可变参数 `vararg`
- 函数类型与函数引用 `::functionName`
- 局部函数

### 04-control-flow 控制流

- `if` 作为表达式（有返回值）
- `when` 表达式（替代 switch）
  - 多值匹配、区间匹配、类型匹配
  - 无参数 `when` 替代 if-else 链
- `for` 循环与区间 `..`、`until`、`downTo`、`step`
- `while` 与 `do-while`
- `break`/`continue` 与标签 `@label`
- 解构声明 `val (name, age) = person`

### 05-null-safety 空安全

- 可空类型 `String?` 与 `String`
- 安全调用 `?.`
- Elvis 操作符 `?:`
- 非空断言 `!!`（及其风险）
- `let` 处理可空值
- 安全类型转换 `as?`
- `is` 类型检查与智能转换
- `require`、`check`、`assert` 前置条件
- 平台类型 `!`（Java 互操作）

### 06-classes-objects 类与对象

- 类定义与主构造函数
- 次构造函数 `constructor`
- `init` 初始化块
- 属性与 backing field
- `data class`（自动 equals/hashCode/toString/copy）
- `sealed class` 与 `sealed interface`
- `enum class` 与关联属性
- `object` 单例
- `companion object` 伴生对象（替代 static）
- 嵌套类与内部类 `inner`
- `value class`（内联类，零开销抽象）

### 07-collections 集合

- 不可变集合：`listOf()`、`setOf()`、`mapOf()`
- 可变集合：`mutableListOf()`、`mutableSetOf()`、`mutableMapOf()`
- 集合操作链：`filter`、`map`、`flatMap`、`reduce`、`fold`
- `forEach`、`any`、`all`、`none`、`find`、`firstOrNull`
- `groupBy`、`partition`、`zip`、`chunked`、`windowed`
- `associate`、`toSet`、`toMutableList` 等转换
- `Sequence` 惰性求值（`asSequence()`）
- 集合排序：`sorted`、`sortedBy`、`sortedDescending`
- `destructuring` 遍历 `for ((k, v) in map)`

### 08-lambdas-higher-order Lambda 与高阶函数

- Lambda 表达式语法 `{ params -> body }`
- 单参数 Lambda 的 `it`
- 高阶函数（函数参数、函数返回值）
- `filter`、`map`、`sortedBy` 等标准库高阶函数
- 作用域函数：`let`、`run`、`with`、`apply`、`also`
  - 使用场景对比
- `inline` 函数与内联 Lambda
- SAM 转换（Java 接口的 Lambda 适配）
- `::function` 函数引用
- 匿名函数 `fun(x: Int): Int`

### 09-extensions 扩展

- 扩展函数 `fun String.isEmail(): Boolean`
- 扩展属性 `val String.lastChar: Char`
- 可空类型扩展 `fun String?.orDefault(): String`
- 中缀函数 `infix fun Int.until(n: Int): Progression`
- 运算符重载 `operator fun plus(other: Point): Point`
- `companion object` 中的扩展
- 泛型扩展函数
- 扩展的解析规则（成员优先于扩展）

### 10-generics 泛型

- 泛型类 `class Box<T>(val value: T)`
- 泛型函数 `fun <T> singletonList(item: T): List<T>`
- 类型约束 `where T : Comparable<T>, T : Serializable`
- 型变：`out`（协变）、`in`（逆变）、`*`（星投影）
- `reified` 具体化类型参数（配合 `inline`）
- 类型检查 `is List<String>`（reified）
- `T::class` 获取类引用（reified）
- 泛型与 Java 互操作的平台类型

### 11-coroutines 协程

- 协程概念：轻量级线程
- `launch`、`async`/`await`
- `suspend` 函数
- `runBlocking` 与 `coroutineScope`
- `CoroutineScope` 与 `CoroutineContext`
- `Dispatchers.Default`、`IO`、`Main`
- `delay` vs `Thread.sleep`
- 结构化并发
- `Flow<T>` 流
  - `flow { }` 构建器
  - `map`、`filter`、`combine` 等操作符
  - `stateIn`、`shareIn` 共享流
- `Channel<T>` 通道
- `withContext` 切换上下文
- 异常处理：`CoroutineExceptionHandler`

### 12-delegation 委托

- 属性委托 `by`
- `by lazy` 延迟初始化
- `by observable` / `by vetoable` 监听变化
- `by map` 委托给 Map
- 自定义属性委托 `ReadOnlyProperty`、`ReadWriteProperty`
- 类委托 `class Decorator(val delegate: Interface) : Interface by delegate`
- 委托模式 vs 继承

### 13-testing 测试

- JUnit5 基础：`@Test`、`@BeforeEach`、`@AfterEach`
- 断言：`assertEquals`、`assertTrue`、`assertThrows`
- 参数化测试 `@ParameterizedTest`、`@MethodSource`
- Kotest 风格：Describe/It、Should/Expect
- Mockk 框架：`mockk`、`every`、`verify`
- 测试替身：`Mockk`、`Spyk`、`.slot`、`coEvery`
- 测试协程：`runTest`、`TestDispatcher`
- 集合测试：`assertIterableEquals`
- 测试命名规范与组织

### 14-annotations-reflect 注解与反射

- 内置注解：`@Deprecated`、`@JvmStatic`、`@JvmOverloads`
- 自定义注解 `annotation class`
- 注解目标 `@Target`、`@Retention`
- 反射基础：`::class`、`::class.java`
- KClass、KFunction、KProperty
- 反射调用函数、访问属性
- 注解处理器简介（KSP vs KAPT）
- 编译期注解 vs 运行时注解

### 15-practice 综合实践

- CLI 工具：命令行参数解析、文件操作
- REST API：Ktor / Spring Boot 基础
- 设计模式在 Kotlin 中的实现
- Kotlin 与 Java 混合开发
- Kotlin Multiplatform 简介
- 性能优化与最佳实践

## 参考资源

- [Kotlin 官方文档](https://kotlinlang.org/docs/home.html)
- [Kotlin Koans](https://play.kotlinlang.org/koans)
- [Kotlin 语言指南](https://kotlinlang.org/docs/idioms.html)
- [Kotlin API Reference](https://kotlinlang.org/api/latest/jvm/stdlib/)
