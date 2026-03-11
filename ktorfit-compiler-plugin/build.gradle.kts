plugins {
    kotlin("jvm")
    id("ktorfit.jvm")
    kotlin("kapt")
    id("ktorfit.publishing")
    id("ktorfit.detekt")
    id("ktorfit.licensee")
    id("org.jlleitschuh.gradle.ktlint")
}

mavenPublishing {
    coordinates(version = libs.versions.ktorfitCompiler.get())
}

dependencies {
    compileOnly(libs.autoService)
    kapt(libs.autoService)
    compileOnly(libs.kotlin.compiler.embeddable)
    testImplementation(libs.kctfork.core)
    testImplementation(libs.junit)
    testImplementation(kotlin("reflect"))
}
