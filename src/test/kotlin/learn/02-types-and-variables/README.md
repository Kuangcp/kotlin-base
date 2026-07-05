# 02-types-and-variables 类型与变量

## 知识点

- `val`（不可变）vs `var`（可变）
- 基本类型：`Int`、`Long`、`Double`、`Float`、`Boolean`、`Char`、`String`
- 类型推断与显式声明
- 字符串模板与原始字符串 `"""..."""`
- 类型转换：`toInt()`、`toDouble()`、`toString()`
- 数组类型：`IntArray`、`Array<String>`
- 常量 `const val`

## val vs var

```kotlin
val immutable = "cannot change"   // 可读不可写
var mutable = "can change"        // 可读可写
```

## 类型推断

```kotlin
val a = 42          // 推断为 Int
val b: Long = 42    // 显式声明为 Long
val c = "hello"     // 推断为 String
```

## 字符串模板

```kotlin
val name = "Kotlin"
println("Hello, $name")           // 简单变量
println("1 + 1 = ${1 + 1}")      // 表达式
val multiLine = """              // 原始字符串
    Line 1
    Line 2
""".trimIndent()
```

## 运行

```bash
gradle test --tests "learn.types.*"
```
