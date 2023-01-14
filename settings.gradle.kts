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
//./gradlew clean :sandbox:compileKotlinJvm --no-daemon -Dorg.gradle.debug=true -Dkotlin.compiler.execution.strategy="in-process" -Dkotlin.daemon.jvm.options="-Xdebug,-Xrunjdwp:transport=dt_socket,address=5005,server=y,suspend=n"

rootProject.name = "Ktorfit"
includeBuild("gradle-plugin")
include(":sandbox")
include(":sandbox3")

include(":ktorfit-ksp")
include(":compiler-plugin")
include(":compiler-plugin-native")
include(":ktorfit-lib")
include(":ktorfit-annotations")

