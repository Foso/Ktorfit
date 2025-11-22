import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("kotlinx-serialization")
}

version = "1.0"
val ktorVersion = "3.2.1"

kotlin {

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
                // Only needed when you want to use Kotlin Serialization
                implementation("io.ktor:ktor-client-serialization:$ktorVersion")
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
    compileSdk = 34
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        targetSdk = 34
        namespace = "com.example.person"
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
