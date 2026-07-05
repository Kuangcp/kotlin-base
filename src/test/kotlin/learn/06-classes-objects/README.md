# 06-classes-objects 类与对象

## 知识点

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

## 类定义

```kotlin
class Person(val name: String, var age: Int) {
    init {
        require(age >= 0) { "Age must be non-negative" }
    }

    fun greet() = "Hello, I'm $name ($age)"
}
```

## data class

```kotlin
data class User(val name: String, val email: String)

val user = User("Alice", "alice@example.com")
val copy = user.copy(name = "Bob")  // 修改部分字段
```

## sealed class

```kotlin
sealed class Result {
    data class Success(val data: String) : Result()
    data class Error(val message: String) : Result()
}

when (result) {
    is Result.Success -> println(result.data)
    is Result.Error -> println(result.message)
}
```

## object 单例

```kotlin
object Database {
    fun connect() = "Connected"
}

Database.connect()  // 直接调用，无需实例化
```

## companion object

```kotlin
class MyClass {
    companion object {
        const val MAX_SIZE = 100
        fun create() = MyClass()
    }
}

MyClass.MAX_SIZE    // 类似 static 访问
MyClass.create()
```

## 运行

```bash
gradle test --tests "learn.classes.*"
```
