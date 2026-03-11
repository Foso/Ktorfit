plugins {
    id("ktorfit.kmp")
    alias(libs.plugins.kspPlugin)
    id("ktorfit.publishing")
    alias(libs.plugins.detekt)
    id("app.cash.licensee")
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

licensee {
    allow("Apache-2.0")
    allow("MIT")
    allowUrl("https://opensource.org/license/mit")
}

detekt {
    toolVersion = libs.versions.detekt.get()
    config.from(files("../detekt-config.yml"))
    buildUponDefaultConfig = false
}

kotlin {
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

android {
    namespace = "de.jensklingenberg.ktorfit.common"
    defaultConfig {
        val proguardFile =
            file("src/jvmMain/resources/META-INF/proguard/ktorfit.pro").also {
                if (!it.exists()) {
                    throw NoSuchFileException(
                        file = it,
                        reason = "We have to provide a proguard rules file for the library.",
                    )
                }
            }
        consumerProguardFiles(proguardFile)
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
