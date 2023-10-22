pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
       // mavenLocal()
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        }
    }
}

rootProject.name = "KtorfitMultiplatformExample"
include(":androidApp")
include(":shared")