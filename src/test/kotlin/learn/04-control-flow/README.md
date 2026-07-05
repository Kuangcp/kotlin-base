# 04-control-flow 控制流

## 知识点

- `if` 作为表达式（有返回值）
- `when` 表达式（替代 switch）
  - 多值匹配、区间匹配、类型匹配
  - 无参数 `when` 替代 if-else 链
- `for` 循环与区间 `..`、`until`、`downTo`、`step`
- `while` 与 `do-while`
- `break`/`continue` 与标签 `@label`
- 解构声明 `val (name, age) = person`

## if 表达式

```kotlin
val max = if (a > b) a else b
val result = if (x > 0) {
    "positive"
} else if (x == 0) {
    "zero"
} else {
    "negative"
}
```

## when 表达式

```kotlin
// 基本匹配
when (score) {
    in 90..100 -> "A"
    in 80 until 90 -> "B"
    else -> "C"
}

// 无参数 when（替代 if-else 链）
when {
    score >= 90 -> "A"
    score >= 80 -> "B"
    else -> "C"
}

// 类型匹配
when (obj) {
    is String -> obj.length
    is Int -> obj + 1
    else -> 0
}
```

## 区间

```kotlin
for (i in 1..5) print("$i ")      // 1 2 3 4 5
for (i in 1 until 5) print("$i ") // 1 2 3 4
for (i in 5 downTo 1) print("$i ") // 5 4 3 2 1
for (i in 1..10 step 2) print("$i ") // 1 3 5 7 9
```

## 运行

```bash
gradle test --tests "learn.controlflow.*"
```
