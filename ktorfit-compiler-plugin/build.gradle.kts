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
    coordinates(version = libs.versions.ktorfitCompiler.get())
}

dependencies {
    implementation(libs.autoService)
    ksp(libs.autoServiceKsp)
    compileOnly(libs.kotlin.compiler.embeddable)
    testImplementation(libs.kctfork.core)
    testImplementation(libs.junit)
    testImplementation(kotlin("reflect"))
}
