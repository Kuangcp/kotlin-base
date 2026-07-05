# 11-coroutines 协程

## 知识点

- 协程概念：轻量级线程
- `launch`、`async`/`await`
- `suspend` 函数
- `runBlocking` 与 `coroutineScope`
- `CoroutineScope` 与 `CoroutineContext`
- `Dispatchers.Default`、`IO`、`Main`
- `delay` vs `Thread.sleep`
- 结构化并发
- `Flow<T>` 流
- `Channel<T>` 通道
- `withContext` 切换上下文
- 异常处理：`CoroutineExceptionHandler`

## 基本概念

```kotlin
// suspend 函数
suspend fun fetchData(): String {
    delay(1000) // 非阻塞等待
    return "data"
}

// runBlocking 桥接
runBlocking {
    val data = fetchData()
    println(data)
}
```

## launch 与 async

```kotlin
runBlocking {
    // launch: 启动协程，不返回结果
    val job = launch {
        delay(1000)
        println("Done")
    }

    // async: 启动协程，返回 Deferred
    val deferred = async {
        delay(500)
        42
    }
    val result = deferred.await() // 42
}
```

## Flow

```kotlin
fun numbers(): Flow<Int> = flow {
    for (i in 1..5) {
        delay(100)
        emit(i)
    }
}

runBlocking {
    numbers().collect { println(it) }
}
```

## 运行

```bash
gradle test --tests "learn.coroutines.*"
```
