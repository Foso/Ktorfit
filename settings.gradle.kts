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
//./gradlew clean :workload:compileKotlinJvm --no-daemon -Dorg.gradle.debug=true -Dkotlin.compiler.execution.strategy="in-process" -Dkotlin.daemon.jvm.options="-Xdebug,-Xrunjdwp:transport=dt_socket,address=5005,server=y,suspend=n"

rootProject.name = "Ktorfit"

include(":sandbox")

include(":ktorfit-ksp")
//include(":ktorfit")
include(":ktorfit-lib")
include(":ktorfit-annotations")

