import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    kotlin("jvm")
    id("ktorfit.jvm")
    id("ktorfit.publishing")
    alias(libs.plugins.detekt)
    kotlin("kapt")
    id("app.cash.licensee")
    id("org.jlleitschuh.gradle.ktlint")
}

licensee {
    allow("Apache-2.0")
    allow("MIT")
}

mavenPublishing {
    coordinates(version = libs.versions.ktorfit.get())
}

dependencies {
    implementation(projects.ktorfitAnnotations)
    implementation(libs.kspApi)
    implementation(libs.kotlinPoet)
    implementation(libs.kotlinPoet.ksp)

    compileOnly(libs.autoService)
    kapt(libs.autoService)

    // TEST
    testImplementation(libs.junit)
    testImplementation(libs.kctfork.core)
    testImplementation(libs.kctfork.ksp)
    testImplementation(libs.mockito.kotlin)
}

detekt {
    toolVersion = libs.versions.detekt.get()
    config.from(files("../detekt-config.yml"))
    buildUponDefaultConfig = false
}

tasks.withType<KotlinCompilationTask<*>>().configureEach {
    compilerOptions.freeCompilerArgs.add("-opt-in=org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi")
}
