import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
    id("com.android.kotlin.multiplatform.library")
    id("ktorfit.jvm")
}

kotlin {
    android {
        compileSdk = 36
        minSdk = 21
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        nodejs()
    }

    jvm()

    js(IR) {
        nodejs()
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

val appleTargetNames = kotlin.targets
    .withType<KotlinNativeTarget>()
    .matching { it.konanTarget.family.isAppleFamily && it.publishable }
    .map { it.name }

tasks.register("appleTest") {
    group = "verification"
    description = "Runs tests for all Apple targets."
    dependsOn(provider {
        appleTargetNames.mapNotNull { tasks.findByName("${it}Test") }
    })
}

tasks.register("publishAppleToMavenLocal") {
    group = "publishing"
    description = "Publishes all Apple target publications to the local Maven repository."
    dependsOn(provider {
        appleTargetNames.mapNotNull { tasks.findByName("publish${it.replaceFirstChar { c -> c.uppercaseChar() }}PublicationToMavenLocal") }
    })
}
