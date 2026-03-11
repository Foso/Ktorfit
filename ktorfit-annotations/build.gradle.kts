import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("ktorfit.kmp")
    id("ktorfit.publishing")
    id("ktorfit.licensee")
    id("org.jlleitschuh.gradle.ktlint")
}

kotlin {
    androidLibrary {
        namespace = "de.jensklingenberg.ktorfit.annotations"
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmWasi {
        nodejs()
    }
}

mavenPublishing {
    coordinates(version = libs.versions.ktorfit.get())
}
