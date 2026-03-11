plugins {
    id("ktorfit.kmp")
    id("ktorfit.publishing")
    id("ktorfit.licensee")
    id("org.jlleitschuh.gradle.ktlint")
}

kotlin {
    explicitApi()

    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.ktorfitLibCore)
            }
        }
    }
}

android {
    namespace = "de.jensklingenberg.ktorfit.converters.flow"
}

mavenPublishing {
    coordinates(version = libs.versions.ktorfit.get())
}
