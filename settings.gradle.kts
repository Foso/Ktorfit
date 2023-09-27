pluginManagement {
    val kotlinVersion: String by settings
    plugins {
        kotlin("multiplatform") version kotlinVersion apply false
    }
    repositories {
        google()
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }

    dependencyResolutionManagement {
        repositories {
            google()
            mavenCentral()
            // your repos
        }
    }


}
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

//./gradlew clean :sandbox:compileKotlinJvm --no-daemon -Dorg.gradle.debug=true -Dkotlin.compiler.execution.strategy="in-process" -Dkotlin.daemon.jvm.options="-Xdebug,-Xrunjdwp:transport=dt_socket,address=5005,server=y,suspend=n"

rootProject.name = "Ktorfit"
includeBuild("gradle-plugin") {
    dependencySubstitution {
        substitute(module("de.jensklingenberg.ktorfit:gradle-plugin:1.7.0")).using(project(":"))
    }
}
include(":sandbox")
include(":ktorfit-ksp")
include(":compiler-plugin")
include(":ktorfit-lib-common")
include(":ktorfit-lib")
include(":ktorfit-annotations")
include(":ktorfit-converters:flow")

//./gradlew clean :ktorfit-annotations:publishToMavenLocal :ktorfit-ksp:publishToMavenLocal :ktorfit-lib:publishToMavenLocal :ktorfit-lib-common:publishToMavenLocal :compiler-plugin:publishToMavenLocal