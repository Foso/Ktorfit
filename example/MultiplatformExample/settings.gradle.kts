pluginManagement {
    repositories {
        //mavenLocal()
        google()
        gradlePluginPortal()
        mavenCentral()

        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        }
    }
}

rootProject.name = "KtorfitMultiplatformExample"
include(":androidApp")
include(":shared")