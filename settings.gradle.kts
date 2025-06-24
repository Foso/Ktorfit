import org.gradle.kotlin.dsl.maven

pluginManagement {

    repositories {
        mavenLocal()
        google()

        mavenCentral()
        gradlePluginPortal()
    }

    dependencyResolutionManagement {
        repositories {
            mavenLocal()
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

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        google()
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        }
        maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

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
include(":ktorfit-converters:response")
