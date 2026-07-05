# 05-null-safety 空安全

## 知识点

- 可空类型 `String?` 与 `String`
- 安全调用 `?.`
- Elvis 操作符 `?:`
- 非空断言 `!!`（及其风险）
- `let` 处理可空值
- 安全类型转换 `as?`
- `is` 类型检查与智能转换
- `require`、`check`、`assert` 前置条件

## 可空类型

```kotlin
val nonNull: String = "hello"    // 不能赋 null
val nullable: String? = "hello"  // 可以赋 null
```

## 安全调用与 Elvis

```kotlin
val length = nullable?.length ?: 0   // 安全调用 + 默认值
```

## let 处理可空值

```kotlin
nullable?.let {
    println("Not null: $it")
}
```

## 前置条件

```kotlin
require(age >= 0) { "Age must be non-negative" }
check(name.isNotEmpty()) { "Name must not be empty" }
```

## 运行

```bash
gradle test --tests "learn.nullsafety.*"
```
