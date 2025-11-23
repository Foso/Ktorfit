import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform")
    alias(libs.plugins.kspPlugin)
    id("kotlinx-serialization")
    id("app.cash.licensee")
    id("de.jensklingenberg.ktorfit")
    id("com.android.application")
}
version = "1.0-SNAPSHOT"

ksp {
    arg("Ktorfit_Errors", "1")
    arg("Ktorfit_QualifiedTypeName", "false")
}

licensee {
    allow("Apache-2.0")
    allow("MIT")
    allow("EPL-1.0")
    allow("MIT-0")
    allowUrl("https://opensource.org/license/mit")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions.jvmTarget = JvmTarget.JVM_1_8
}
kotlin {

    applyDefaultHierarchyTemplate()
    androidTarget()
    jvm()
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.ktorfitLibCore)
                implementation(projects.ktorfitConverters.flow)
                implementation(projects.ktorfitConverters.call)
                implementation(projects.ktorfitConverters.response)
                implementation(projects.ktorfitAnnotations)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.ktor.client.serialization)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
            }
        }
        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotlin.coroutines.test)
                implementation(libs.ktor.client.mock)
            }
        }
        linuxX64Main {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.ktor.client.curl)
                implementation(libs.ktor.client.core.linuxX64)
                implementation(libs.ktor.client.cio.linuxX64)
            }
        }

        jvmMain {
            kotlin.srcDir("build/generated/ksp/jvm/jvmMain/")

            dependencies {
                implementation(libs.ktor.client.core.jvm)
                implementation("ch.qos.logback:logback-classic:1.2.3")
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.serialization.gson)
                implementation(libs.ktor.client.cio.jvm)
            }
        }

        jvmTest {
            dependencies {
                implementation(libs.ktor.client.mock)
                implementation(libs.junit)
            }
        }

        jsMain {
            dependencies {
                implementation(libs.kotlinx.serialization.runtime.js)
                implementation(libs.ktor.client.serialization)
                implementation(libs.ktor.client.json.js)
                implementation(libs.ktor.client.js)
            }
        }
        androidMain {
           // kotlin.srcDir("build/generated/ksp/android")
        }
    }
}

android {
    compileSdk = 34
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        targetSdk = 34
        namespace = "com.example.ktorfittest"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

configurations.all {
    resolutionStrategy.dependencySubstitution {
        substitute(module("de.jensklingenberg.ktorfit:gradle-plugin"))
            .using(project(":ktorfit-gradle-plugin"))
            .because("we work with the unreleased development version")
        substitute(module("de.jensklingenberg.ktorfit:compiler-plugin"))
            .using(project(":ktorfit-compiler-plugin"))
            .because("we work with the unreleased development version")
        substitute(module("de.jensklingenberg.ktorfit:converters-flow"))
            .using(project(":ktorfit-converters:flow"))
            .because("we work with the unreleased development version")
        substitute(module("de.jensklingenberg.ktorfit:converters-call"))
            .using(project(":ktorfit-converters:call"))
            .because("we work with the unreleased development version")
    }
}
