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
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.2")
    implementation(kotlin("reflect"))

    // Logging
    implementation("io.github.oshai:kotlin-logging-jvm:7.0.0")
    implementation("ch.qos.logback:logback-classic:1.5.16")
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