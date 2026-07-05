# 10-generics 泛型

## 知识点

- 泛型类 `class Box<T>(val value: T)`
- 泛型函数 `fun <T> singletonList(item: T): List<T>`
- 类型约束 `where T : Comparable<T>, T : Serializable`
- 型变：`out`（协变）、`in`（逆变）、`*`（星投影）
- `reified` 具体化类型参数（配合 `inline`）
- 类型检查 `is List<String>`（reified）
- `T::class` 获取类引用（reified）

## 泛型类

```kotlin
class Box<T>(val value: T)
val intBox = Box(42)         // Box<Int>
val strBox = Box("hello")   // Box<String>
```

## 泛型函数

```kotlin
fun <T> singletonList(item: T): List<T> = listOf(item)
val list = singletonList("hello")  // List<String>
```

## 型变

```kotlin
// out: 协变，Producer<T> 可以赋值给 Producer<Parent>
interface Producer<out T> { fun produce(): T }

// in: 逆变，Consumer<T> 可以赋值给 Consumer<Child>
interface Consumer<in T> { fun consume(item: T) }

// *: 星投影，不确定类型参数
fun printAll(list: List<*>) { for (item in list) print("$item ") }
```

## reified

```kotlin
inline fun <reified T> Any.isType(): Boolean = this is T
42.isType<Int>()      // true
"hello".isType<String>()  // true

inline fun <reified T> filterByType(list: List<Any>): List<T> =
    list.filterIsInstance<T>()

val mixed = listOf(1, "hello", 3.14, "world")
val strings = filterByType<String>(mixed)  // ["hello", "world"]
```

## 运行

```bash
gradle test --tests "learn.generics.*"
```
