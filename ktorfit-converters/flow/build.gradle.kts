plugins {
    id("ktorfit.kmp")
    id("ktorfit.publishing")
    id("ktorfit.licensee")
    id("org.jlleitschuh.gradle.ktlint")
}

kotlin {
    androidLibrary {
        namespace = "de.jensklingenberg.ktorfit.converters.flow"
    }

    explicitApi()

    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.ktorfitLibCore)
            }
        }
    }
}

mavenPublishing {
    coordinates(version = libs.versions.ktorfit.get())
}
