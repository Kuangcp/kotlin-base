# 03-functions 函数

## 知识点

- 函数定义语法 `fun name(param: Type): ReturnType`
- 单表达式函数 `fun add(a: Int, b: Int) = a + b`
- 默认参数与命名参数
- `Unit` 与 `Nothing` 返回类型
- 可变参数 `vararg`
- 函数类型与函数引用 `::functionName`
- 局部函数

## 基本函数

```kotlin
fun add(a: Int, b: Int): Int {
    return a + b
}

fun greet(name: String = "World") {
    println("Hello, $name!")
}
```

## 单表达式函数

```kotlin
fun multiply(a: Int, b: Int) = a * b
fun isEven(n: Int) = n % 2 == 0
```

## 默认参数与命名参数

```kotlin
fun createUser(name: String, age: Int = 0, active: Boolean = true) = "$name ($age, active=$active)"

// 调用
createUser("Alice")                          // 使用默认值
createUser("Bob", age = 25, active = false)  // 命名参数
```

## vararg 可变参数

```kotlin
fun sum(vararg numbers: Int): Int = numbers.sum()
sum(1, 2, 3)       // 6
sum(1, 2, 3, 4, 5) // 15
```

## 函数引用

```kotlin
fun double(n: Int) = n * 2
val doubled = listOf(1, 2, 3).map(::double)  // [2, 4, 6]
```

## 运行

```bash
gradle test --tests "learn.functions.*"
```
