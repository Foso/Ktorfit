import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform")
    id("com.android.kotlin.multiplatform.library")
    id("kotlinx-serialization")
}

version = "1.0"
val ktorVersion = "3.5.0"

kotlin {
    jvmToolchain(11)
    applyDefaultHierarchyTemplate()

    jvm()
    android {
        namespace = "com.example.person"
        compileSdk = 34
        minSdk = 21
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    macosArm64()
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
        val macosArm64Main by getting
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
