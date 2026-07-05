# 14-annotations-reflect 注解与反射

## 知识点

- 内置注解：`@Deprecated`、`@JvmStatic`、`@JvmOverloads`
- 自定义注解 `annotation class`
- 注解目标 `@Target`、`@Retention`
- 反射基础：`::class`、`::class.java`
- KClass、KFunction、KProperty
- 反射调用函数、访问属性

## 自定义注解

```kotlin
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ApiEndpoint(val path: String)
```

## 反射

```kotlin
class Person(val name: String, val age: Int)

val person = Person("Alice", 30)
val kClass = person::class

// 属性
kClass.memberProperties.forEach { prop ->
    println("${prop.name} = ${prop.get(person)}")
}
```

## 运行

```bash
gradle test --tests "learn.annotations.*"
```
