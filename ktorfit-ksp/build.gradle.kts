import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    kotlin("jvm")
    alias(libs.plugins.kspPlugin)
    id("ktorfit.jvm")
    id("ktorfit.publishing")
    id("ktorfit.detekt")
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

    implementation(libs.autoService)
    ksp(libs.autoServiceKsp)

    // TEST
    testImplementation(libs.junit)
    testImplementation(libs.kctfork.core)
    testImplementation(libs.kctfork.ksp)
    testImplementation(libs.mockito.kotlin)
}

tasks.named<KotlinCompilationTask<*>>("compileTestKotlin") {
    compilerOptions.freeCompilerArgs.add("-opt-in=org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi")
}
