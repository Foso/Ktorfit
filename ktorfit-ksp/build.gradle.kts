import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    kotlin("jvm")
    id("ktorfit.jvm")
    id("ktorfit.publishing")
    id("ktorfit.detekt")
    kotlin("kapt")
    id("ktorfit.licensee")
    id("org.jlleitschuh.gradle.ktlint")
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

tasks.withType<KotlinCompilationTask<*>>().configureEach {
    compilerOptions.freeCompilerArgs.add("-opt-in=org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi")
}
