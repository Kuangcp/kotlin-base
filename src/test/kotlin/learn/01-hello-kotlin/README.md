# 01-hello-kotlin 环境搭建

## 知识点

- Kotlin/JVM 环境配置
- Hello World 程序
- `main` 函数
- `println` 输出
- String 模板 `$variable`、`${expression}`
- `readln()` 读取输入

## 基本语法

```kotlin
fun main() {
    println("Hello, Kotlin!")

    val name = "Kotlin"
    val version = 2.1
    println("Language: $name, Version: $version")
    println("1 + 1 = ${1 + 1}")
}
```

## 运行

```bash
gradle test --tests "learn.hello.*"
```
