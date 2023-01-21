pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
       // mavenLocal()
    }
}

rootProject.name = "KtorfitMultiplatformExample"
include(":androidApp")
include(":shared")