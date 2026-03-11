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
        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        jvmTest {
            dependencies {
                implementation(libs.ktor.client.mock)
                implementation(libs.mockk)
                implementation(libs.mockito.kotlin)
            }
        }
    }
}

android {
    namespace = "de.jensklingenberg.ktorfit.converters.call"
}

mavenPublishing {
    coordinates(version = libs.versions.ktorfit.get())
}
