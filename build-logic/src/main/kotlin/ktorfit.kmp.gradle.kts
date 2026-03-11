import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("ktorfit.jvm")
}

kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        nodejs()
    }

    jvm()

    js(IR) {
        nodejs()
    }

    androidTarget {
        publishLibraryVariants("release", "debug")
    }

    androidNativeArm32()
    androidNativeArm64()
    androidNativeX86()
    androidNativeX64()

    iosArm64()
    iosX64()
    iosSimulatorArm64()

    watchosArm32()
    watchosArm64()
    watchosSimulatorArm64()
    watchosDeviceArm64()

    tvosArm64()
    tvosSimulatorArm64()

    macosArm64()

    linuxX64()
    linuxArm64()

    mingwX64()

    applyDefaultHierarchyTemplate()
}

android {
    compileSdk = 34
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
    }
}

