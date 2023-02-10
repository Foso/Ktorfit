pluginManagement {
    val kotlinVersion: String by settings
    val kspVersion: String by settings
    plugins {
        id("com.google.devtools.ksp") version kspVersion apply false
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
        substitute(module("de.jensklingenberg.ktorfit:gradle-plugin:1.0.0")).using(project(":"))
    }
}
include(":sandbox")
include(":ktorfit-ksp")
include(":compiler-plugin")
include(":ktorfit-lib")
include(":ktorfit-annotations")

