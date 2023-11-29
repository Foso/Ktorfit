pluginManagement {

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
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "de.jensklingenberg.ktorfit") {
                useModule("de.jensklingenberg.ktorfit:de.jensklingenberg.ktorfit.gradle.plugin:${requested.version}")
            }
        }

    }

}
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.7.0"
}

rootProject.name = "Ktorfit"
include(":ktorfit-gradle-plugin")
include(":sandbox")
include(":ktorfit-ksp")
include(":ktorfit-compiler-plugin")
include(":ktorfit-lib-core")
include(":ktorfit-lib")
include(":ktorfit-annotations")
include(":ktorfit-converters:flow")
include(":ktorfit-converters:call")
