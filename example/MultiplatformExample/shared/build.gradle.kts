import de.jensklingenberg.ktorfit.gradle.ErrorCheckingMode
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("com.google.devtools.ksp") version "2.3.3"
    id("kotlinx-serialization")
    id("de.jensklingenberg.ktorfit") version "2.7.1"
}

ktorfit {
    errorCheckingMode = ErrorCheckingMode.ERROR
}

version = "1.0"
val ktorVersion = "3.3.3"
val ktorfitVersion = "2.7.1"

kotlin {
    jvmToolchain(11)
    applyDefaultHierarchyTemplate()

    jvm()
    androidTarget()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    macosX64()
    js(IR) {
        this.nodejs()
        binaries.executable()
    }


    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":person"))
                implementation("de.jensklingenberg.ktorfit:ktorfit-lib:$ktorfitVersion")
                // implementation("de.jensklingenberg.ktorfit:ktorfit-lib-light:$ktorfitVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
                implementation("de.jensklingenberg.ktorfit:ktorfit-converters-response:$ktorfitVersion")
                implementation("de.jensklingenberg.ktorfit:ktorfit-converters-call:$ktorfitVersion")
                implementation("de.jensklingenberg.ktorfit:ktorfit-converters-flow:$ktorfitVersion")

                // Only needed when you want to use Kotlin Serialization
                implementation("io.ktor:ktor-client-serialization:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting
        val jvmMain by getting
        val jsMain by getting
        val iosMain by getting
        val macosX64Main by getting
    }
}

android {
    compileSdk = 36
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

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
        allWarningsAsErrors.set(false)
    }
}

allprojects {
    repositories {
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        }
    }
}
