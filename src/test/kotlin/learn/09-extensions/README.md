# 09-extensions 扩展

## 知识点

- 扩展函数 `fun String.isEmail(): Boolean`
- 扩展属性 `val String.lastChar: Char`
- 可空类型扩展 `fun String?.orDefault(): String`
- 中缀函数 `infix fun Int.until(n: Int): Progression`
- 运算符重载 `operator fun plus(other: Point): Point`
- 泛型扩展函数
- 扩展的解析规则（成员优先于扩展）

## 扩展函数

```kotlin
fun String.isEmail(): Boolean = contains("@") && contains(".")
fun Int.isEven(): Boolean = this % 2 == 0

"test@example.com".isEmail()  // true
42.isEven()                    // true
```

## 扩展属性

```kotlin
val String.lastChar: Char
    get() = this[length - 1]

"Kotlin".lastChar  // 'n'
```

## 中缀函数

```kotlin
infix fun Int.power(n: Int): Int {
    var result = 1
    repeat(n) { result *= this }
    return result
}

2 power 8  // 256
```

## 运算符重载

```kotlin
data class Point(val x: Int, val y: Int) {
    operator fun plus(other: Point) = Point(x + other.x, y + other.y)
}

Point(1, 2) + Point(3, 4)  // Point(4, 6)
```

## 运行

```bash
gradle test --tests "learn.extensions.*"
```
