plugins {
    kotlin("multiplatform") version libs.versions.kotlin apply false
    alias(libs.plugins.mavenPublish) apply false
    id("org.jlleitschuh.gradle.ktlint") version "12.2.0" apply false
    alias(libs.plugins.binaryCompatibilityValidator)
}

buildscript {
    repositories {
        mavenLocal()
        google()
        gradlePluginPortal()

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

apiValidation {
    ignoredProjects.addAll(
        listOf(
            "sandbox",
        )
    )
    @OptIn(kotlinx.validation.ExperimentalBCVApi::class)
    klib {
        enabled = true
    }
}
