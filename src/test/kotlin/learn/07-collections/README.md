# 07-collections 集合

## 知识点

- 不可变集合：`listOf()`、`setOf()`、`mapOf()`
- 可变集合：`mutableListOf()`、`mutableSetOf()`、`mutableMapOf()`
- 集合操作链：`filter`、`map`、`flatMap`、`reduce`、`fold`
- `forEach`、`any`、`all`、`none`、`find`、`firstOrNull`
- `groupBy`、`partition`、`zip`、`chunked`、`windowed`
- `associate`、`toSet`、`toMutableList` 等转换
- `Sequence` 惰性求值（`asSequence()`）
- 集合排序：`sorted`、`sortedBy`、`sortedDescending`
- `destructuring` 遍历 `for ((k, v) in map)`

## 不可变 vs 可变

```kotlin
val immutableList = listOf(1, 2, 3)       // 只读
val mutableList = mutableListOf(1, 2, 3)  // 可增删
mutableList.add(4)
```

## 操作链

```kotlin
val result = listOf(1, 2, 3, 4, 5, 6)
    .filter { it % 2 == 0 }   // [2, 4, 6]
    .map { it * it }          // [4, 16, 36]
    .sum()                    // 56
```

## Sequence 惰性求值

```kotlin
val result = (1..1_000_000).asSequence()
    .filter { it % 2 == 0 }
    .take(5)
    .toList()
```

## 运行

```bash
gradle test --tests "learn.collections.*"
```
