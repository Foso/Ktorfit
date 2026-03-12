plugins {
    kotlin("jvm")
    id("ktorfit.jvm")
    id("java-gradle-plugin")
    `kotlin-dsl`
    alias(libs.plugins.gradlePluginPublish)
    id("ktorfit.publishing")
    id("org.jlleitschuh.gradle.ktlint")
}

dependencies {
    implementation(libs.kotlin.gradle.plugin)
}

gradlePlugin {
    website.set("https://github.com/Foso/Ktorfit")
    vcsUrl.set("https://github.com/Foso/Ktorfit")
    plugins {

        create("ktorfitPlugin") {
            id = "de.jensklingenberg.ktorfit" // users will do `apply plugin: "de.jensklingenberg.ktorfit"`
            implementationClass = "de.jensklingenberg.ktorfit.gradle.KtorfitGradlePlugin" // entry-point class
            displayName = "Ktorfit Gradle Plugin"
            description = "Gradle Plugin for Ktorfit"
            tags.set(listOf("http", "kotlin", "kotlin-mpp", "ktor", "rest"))
        }
    }
}

mavenPublishing {
    coordinates(version = libs.versions.ktorfitGradlePlugin.get())
}
