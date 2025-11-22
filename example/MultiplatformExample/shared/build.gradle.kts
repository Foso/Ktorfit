import de.jensklingenberg.ktorfit.gradle.ErrorCheckingMode
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("com.google.devtools.ksp") version "2.2.0-2.0.2"
    id("kotlinx-serialization")
    id("de.jensklingenberg.ktorfit") version "2.6.4"
}

ktorfit {
    errorCheckingMode = ErrorCheckingMode.ERROR
}

version = "1.0"
val ktorVersion = "3.2.1"
val ktorfitVersion = "2.6.4"

kotlin {
    applyDefaultHierarchyTemplate()


    androidTarget()



    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":person"))
                implementation("de.jensklingenberg.ktorfit:ktorfit-lib:$ktorfitVersion")
                // implementation("de.jensklingenberg.ktorfit:ktorfit-lib-light:$ktorfitVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
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
