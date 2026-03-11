import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("ktorfit.kmp")
    id("ktorfit.publishing")
    id("app.cash.licensee")
    id("org.jlleitschuh.gradle.ktlint")
}

licensee {
    allow("Apache-2.0")
    allow("MIT")
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
