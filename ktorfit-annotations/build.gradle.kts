import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("ktorfit.kmp")
    id("ktorfit.publishing")
    id("ktorfit.licensee")
    id("org.jlleitschuh.gradle.ktlint")
}

kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmWasi {
        nodejs()
    }
}

android {
    namespace = "de.jensklingenberg.ktorfit.annotations"
}

mavenPublishing {
    coordinates(version = libs.versions.ktorfit.get())
}
