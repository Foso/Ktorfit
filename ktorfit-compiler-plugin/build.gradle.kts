plugins {
    kotlin("jvm")
    id("ktorfit.jvm")
    kotlin("kapt")
    id("ktorfit.publishing")
    alias(libs.plugins.detekt)
    id("app.cash.licensee")
    id("org.jlleitschuh.gradle.ktlint")
}

licensee {
    allow("Apache-2.0")
    allow("MIT")
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

detekt {
    toolVersion = libs.versions.detekt.get()
    config.from(files("../detekt-config.yml"))
    buildUponDefaultConfig = false
}
