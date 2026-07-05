# 12-delegation 委托

## 知识点

- 属性委托 `by`
- `by lazy` 延迟初始化
- `by observable` / `by vetoable` 监听变化
- `by map` 委托给 Map
- 自定义属性委托 `ReadOnlyProperty`、`ReadWriteProperty`
- 类委托 `class Decorator(val delegate: Interface) : Interface by delegate`
- 委托模式 vs 继承

## by lazy

```kotlin
val expensiveValue: Int by lazy {
    println("Computing...")
    42
}
// 首次访问时才计算
println(expensiveValue) // 输出 Computing... 然后 42
println(expensiveValue) // 直接输出 42
```

## by observable

```kotlin
var name: String by observable("初始") { _, old, new ->
    println("$old -> $new")
}
name = "新值" // 输出: 初始 -> 新值
```

## 自定义委托

```kotlin
class Validated(private val validator: (String) -> Boolean) : ReadWriteProperty<Any?, String> {
    private var field: String = ""

    override fun getValue(thisRef: Any?, property: KProperty<*>): String = field
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        require(validator(value)) { "Invalid value: $value" }
        field = value
    }
}
```

## 运行

```bash
gradle test --tests "learn.delegation.*"
```
