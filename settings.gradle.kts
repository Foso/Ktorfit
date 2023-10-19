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


rootProject.name = "Ktorfit"
include("gradle-plugin")
include(":sandbox")
include(":ktorfit-ksp")
include(":compiler-plugin")
include(":ktorfit-lib-core")
include(":ktorfit-lib")
include(":ktorfit-annotations")
include(":ktorfit-converters:flow")
include(":ktorfit-converters:call")
