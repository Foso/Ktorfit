plugins {
    kotlin("multiplatform")
    alias(libs.plugins.kspPlugin)
    id("kotlinx-serialization")
}
apply(plugin = "de.jensklingenberg.ktorfit")
version = "1.0-SNAPSHOT"

configure<de.jensklingenberg.ktorfit.gradle.KtorfitGradleConfiguration> {
    enabled = true
    version = libs.versions.ktorfit.asProvider().get()
}
ksp {
    arg("Ktorfit_Errors", "1")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        withJava()
    }
    iosX64()
    iosArm64()
    js(IR) {
        this.nodejs()
        binaries.executable() // not applicable to BOTH, see details below
    }
    linuxX64() {
        binaries {
            executable()
        }
    }

    macosX64("macOS")
    mingwX64()
    sourceSets {
        val commonMain by getting {

            dependencies {
                implementation(projects.ktorfitLibCommon)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.ktor.client.serialization)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)

            }
        }
        val linuxX64Main by getting {
            dependencies {

                implementation(libs.ktor.client.core.native)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.ktor.client.curl)
                implementation(libs.ktor.client.core.linux)
                implementation(libs.ktor.client.cio.linux)

            }
        }


        val jvmMain by getting {
            kotlin.srcDir("build/generated/ksp/jvm/jvmMain/")

            dependencies {
                implementation(libs.ktor.client.core.jvm)
                implementation(libs.kotlinx.coroutines.rx3)
                implementation(libs.rxjava3)

                implementation(libs.ktor.client.logging)
                implementation(libs.logbackClassic)
                implementation(libs.ktor.serialization.gson)
                implementation(libs.ktor.client.cio.jvm)
            }
        }

        val jvmTest by getting {
            //   kotlin.srcDir("build/kspCaches/jvmMain/")


            dependencies {
                dependsOn(jvmMain)
                implementation(libs.ktor.client.mock)
                implementation(libs.junit)


            }
        }

        val jsMain by getting {
            dependencies {
                implementation(libs.kotlinx.serialization.runtime.js)
                implementation(libs.ktor.client.serialization)
                implementation(libs.ktor.client.json.js)
                implementation(libs.ktor.client.js)

            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
        }

    }
}


dependencies {

    add(
        "kspCommonMainMetadata", projects.ktorfitKsp
    )
    add("kspJvm", projects.ktorfitKsp)
    add("kspIosX64", projects.ktorfitKsp)

    // add("kspJvmTest", projects.ktorfitKsp)
    add("kspJs", projects.ktorfitKsp)
    add("kspLinuxX64", projects.ktorfitKsp)
    add("kspMingwX64", projects.ktorfitKsp)

}
