plugins {
    kotlin("multiplatform") version libs.versions.kotlin apply false
    alias(libs.plugins.mavenPublish) apply false
}

buildscript {
    repositories {
        google()
        gradlePluginPortal()
        mavenLocal()
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
        }
        maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
    }
    dependencies {
        classpath(libs.gradle.maven.publish.plugin)
        classpath(libs.kotlin.serialization)
        classpath(libs.android.build.gradle)
        classpath(libs.licensee.gradle.plugin)
        classpath(libs.ktorfit.gradle.plugin)
    }
}

subprojects {
    repositories {
        google()
        mavenLocal()
        mavenCentral()
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        }
        maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
    }
}
