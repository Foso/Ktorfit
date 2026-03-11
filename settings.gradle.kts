pluginManagement {
    includeBuild("build-logic")

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        }
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "Ktorfit"
include(":ktorfit-gradle-plugin")
include(":ktorfit-ksp")

// Only include sandbox if not running a publish task

include(":ktorfit-compiler-plugin")
include(":ktorfit-lib-core")
include(":ktorfit-lib")
include(":ktorfit-annotations")
include(":ktorfit-converters:flow")
include(":ktorfit-converters:call")
include(":ktorfit-converters:response")
include(":sandbox")
