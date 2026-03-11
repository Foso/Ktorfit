plugins {
    id("ktorfit.kmp")
    alias(libs.plugins.kspPlugin)
    id("ktorfit.publishing")
    id("ktorfit.detekt")
    id("ktorfit.licensee")
    id("org.jlleitschuh.gradle.ktlint")
}

ktlint {
    filter {
        exclude {
            it.file.path.contains(
                layout.buildDirectory
                    .dir("generated")
                    .get()
                    .toString(),
            )
        }
    }
}

kotlin {
    androidLibrary {
        namespace = "de.jensklingenberg.ktorfit.common"
    }

    explicitApi()

    sourceSets {
        commonMain {
            dependencies {
                api(projects.ktorfitAnnotations)
                api(libs.ktor.client.core)
            }
        }
        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        jvmTest {
            dependencies {
                kotlin.srcDir("build/generated/ksp/jvm/jvmTest/")

                implementation(libs.ktor.client.mock)
                implementation(libs.junit)
                implementation(libs.mockito.kotlin)
                implementation(libs.ktor.client.cio)
                implementation(libs.kotlin.coroutines.test)
            }
        }
    }
}

mavenPublishing {
    coordinates(version = libs.versions.ktorfit.get())
}

ksp {
    arg("Ktorfit_QualifiedTypeName", "true")
}

dependencies {
    add(
        "kspCommonMainMetadata",
        projects.ktorfitKsp,
    )
    add("kspJvm", projects.ktorfitKsp)
    add("kspJvmTest", projects.ktorfitKsp)
}
