plugins {
    id("ktorfit.kmp")
    id("ktorfit.publishing")
    id("ktorfit.licensee")
}

kotlin {
    android {
        namespace = "de.jensklingenberg.ktorfit"
    }

    explicitApi()

    sourceSets {
        commonMain {
            dependencies {
                api(projects.ktorfitLibCore)
            }
        }
        linuxMain {
            dependencies {
                implementation(libs.ktor.client.cio)
            }
        }

        mingwX64Main {
            dependencies {
                implementation(libs.ktor.client.core)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.ktor.client.okhttp)
            }
        }
        jvmMain {
            dependencies {
                implementation(libs.ktor.client.cio)
            }
        }

        jsMain {
            dependencies {
                implementation(libs.ktor.client.js)
            }
        }

        iosMain {
            dependencies {
                implementation(libs.ktor.client.ios)
            }
        }
    }
}

mavenPublishing {
    coordinates(version = libs.versions.ktorfit.get())
}
