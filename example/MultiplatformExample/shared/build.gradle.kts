plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")
    id("com.google.devtools.ksp") version "2.0.0-RC3-1.0.20"
    id("kotlinx-serialization")
    id("de.jensklingenberg.ktorfit") version "2.0.0-2.0.0-SNAPSHOT"
}

version = "1.0"
val ktorVersion = "2.3.10"
val ktorfitVersion = "2.0.0-SNAPSHOT"
val ktorfitKsp = "2.0.0-1.0.20-SNAPSHOT"

kotlin {
    jvmToolchain(8)
    targetHierarchy.default()

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
    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "shared"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("de.jensklingenberg.ktorfit:ktorfit-lib:$ktorfitVersion")
                //implementation("de.jensklingenberg.ktorfit:ktorfit-lib-light:$ktorfitVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
                implementation("de.jensklingenberg.ktorfit:ktorfit-converters-response:2.0.0-SNAPSHOT")
                implementation("de.jensklingenberg.ktorfit:ktorfit-converters-call:2.0.0-SNAPSHOT")
                implementation("de.jensklingenberg.ktorfit:ktorfit-converters-flow:2.0.0-SNAPSHOT")

                //Only needed when you want to use Kotlin Serialization
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
    compileSdk = 34
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        targetSdk = 34
    }
}

dependencies {
    with("de.jensklingenberg.ktorfit:ktorfit-ksp:$ktorfitKsp") {
        add("kspCommonMainMetadata", this)
        add("kspJvm", this)
        add("kspJvmTest", this)
        add("kspAndroid", this)
        add("kspAndroidTest", this)
        add("kspIosX64", this)
        add("kspIosX64Test", this)
        add("kspIosArm64", this)
        add("kspIosArm64Test", this)
        add("kspIosSimulatorArm64", this)
        add("kspIosSimulatorArm64Test", this)
        add("kspMacosX64", this)
        add("kspMacosX64Test", this)
        add("kspJs", this)
        add("kspJsTest", this)
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        allWarningsAsErrors = false
    }
}

allprojects {
    repositories {
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        }
    }
}