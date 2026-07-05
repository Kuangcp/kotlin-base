pluginManagement {
    repositories {
        maven("https://maven.aliyun.com/repository/gradle-plugin")
        maven("https://maven.aliyun.com/repository/public")
        maven("https://maven.aliyun.com/repository/google")
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}

//plugins {
//    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
//}
rootProject.name = "kotlin-base"
