buildscript {
    dependencies {
        // Required for sandbox to apply the ktorfit plugin from the local project
        classpath(libs.ktorfit.gradle.plugin)
    }
}

plugins {
    kotlin("multiplatform") version libs.versions.kotlin apply false
    kotlin("plugin.serialization") version libs.versions.kotlin apply false
    alias(libs.plugins.mavenPublish) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.binaryCompatibilityValidator)
}

apiValidation {
    ignoredProjects.addAll(listOf("sandbox","ktorfit-ksp","ktorfit-compiler-plugin"))

    @OptIn(kotlinx.validation.ExperimentalBCVApi::class)
    klib {
        enabled = true
    }
}
