plugins {
    kotlin("jvm") version "2.1.0"
    application
}

group = "com.github.kuangcp"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

tasks.register<JavaExec>("runCoroutines") {
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.github.kuangcp.coroutines.GuideDemoKt")
}