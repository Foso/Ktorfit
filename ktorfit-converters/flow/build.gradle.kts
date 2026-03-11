plugins {
    id("ktorfit.kmp")
    id("ktorfit.publishing")
    id("app.cash.licensee")
    id("org.jlleitschuh.gradle.ktlint")
}

licensee {
    allow("Apache-2.0")
    allow("MIT")
    allowUrl("https://opensource.org/license/mit")
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
