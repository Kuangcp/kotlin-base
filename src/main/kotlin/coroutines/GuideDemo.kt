package com.github.kuangcp.coroutines

import kotlinx.coroutines.*

/**
 *
 * @author <a href="https://github.com/kuangcp">Kuangcp</a> on 2024-05-26 16:26
 */

@OptIn(DelicateCoroutinesApi::class)
fun main() {
    GlobalScope.launch { // 在后台启动一个新的协程并继续
        delay(1000L) // 非阻塞的等待 1 秒钟（默认时间单位是毫秒）
        println("World!") // 在延迟后打印输出
    }
    println("Hello,") // 协程已在等待时主线程还在继续
    Thread.sleep(2000L) // 阻塞主线程 2 秒钟来保证 JVM 存活
}