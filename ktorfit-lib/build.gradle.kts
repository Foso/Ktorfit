plugins {
    id("ktorfit.kmp")
    id("ktorfit.publishing")
    id("app.cash.licensee")
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

android {
    namespace = "de.jensklingenberg.ktorfit"
}

mavenPublishing {
    coordinates(version = libs.versions.ktorfit.get())
}
