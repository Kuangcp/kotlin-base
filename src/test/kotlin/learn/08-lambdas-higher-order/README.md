# 08-lambdas-higher-order Lambda 与高阶函数

## 知识点

- Lambda 表达式语法 `{ params -> body }`
- 单参数 Lambda 的 `it`
- 高阶函数（函数参数、函数返回值）
- 作用域函数：`let`、`run`、`with`、`apply`、`also`
- `inline` 函数与内联 Lambda
- SAM 转换（Java 接口的 Lambda 适配）
- `::function` 函数引用
- 匿名函数 `fun(x: Int): Int`

## Lambda 语法

```kotlin
val add = { a: Int, b: Int -> a + b }
val square = { x: Int -> x * x }
val printHello = { println("Hello!") }
```

## 作用域函数对比

```kotlin
// let: 转换对象，适合可空处理
val length = str?.let { it.length }

// run: 在对象上下文中执行代码
val result = obj.run { "$name: $age" }

// with: 非扩展函数，用于多个操作
with(obj) { name = "new"; age = 25 }

// apply: 配置对象，返回对象本身
val person = Person().apply { name = "Alice"; age = 30 }

// also: 附加操作，返回对象本身
val list = mutableListOf(1, 2, 3).also { it.add(4) }
```

## 运行

```bash
gradle test --tests "learn.lambdas.*"
```
