plugins {
    kotlin("multiplatform")
    alias(libs.plugins.kspPlugin)
    id("kotlinx-serialization")
    id("app.cash.licensee")
    id("de.jensklingenberg.ktorfit")
}
version = "1.0-SNAPSHOT"


ksp {
    arg("Ktorfit_Errors", "1")
}

licensee {
    allow("Apache-2.0")
    allow("MIT")
    allow("EPL-1.0")
    allow("MIT-0")
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
    linuxArm64() {
        binaries {
            executable()
        }
    }

    // macosX64()
    mingwX64()
    applyDefaultHierarchyTemplate()
    sourceSets {
        val commonMain by getting {

            dependencies {
                implementation(projects.ktorfitLibCore)
                implementation(projects.ktorfitConverters.flow)
                implementation(projects.ktorfitConverters.call)
                implementation(projects.ktorfitConverters.response)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.ktor.client.serialization)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)

            }
        }
        val linuxX64Main by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.ktor.client.curl)
                implementation(libs.ktor.client.core.linuxX64)
                implementation(libs.ktor.client.cio.linuxX64)

            }
        }


        val jvmMain by getting {
            kotlin.srcDir("build/generated/ksp/jvm/jvmMain/")

            dependencies {
                implementation(libs.ktor.client.core.jvm)
                implementation(libs.kotlinx.coroutines.rx3)
                implementation("io.reactivex.rxjava3:rxjava:3.1.8")

                implementation(libs.ktor.client.logging)
                // implementation(libs.logbackClassic)
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
        val iosMain by getting {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
        }

    }
}


dependencies {
    with(projects.ktorfitKsp) {
        add(
            "kspCommonMainMetadata", this
        )
        add("kspJvm", this)
        add("kspIosX64", this)

        // add("kspJvmTest", this)
        add("kspJs", this)
        add("kspLinuxX64", this)
        add("kspMingwX64", this)
    }


}



configurations.all {
    resolutionStrategy.dependencySubstitution {
        substitute(module("de.jensklingenberg.ktorfit:gradle-plugin"))
            .using(project(":ktorfit-gradle-plugin")).because("we work with the unreleased development version")
        substitute(module("de.jensklingenberg.ktorfit:compiler-plugin"))
            .using(project(":ktorfit-compiler-plugin")).because("we work with the unreleased development version")
        substitute(module("de.jensklingenberg.ktorfit:converters-flow"))
            .using(project(":ktorfit-converters:flow")).because("we work with the unreleased development version")
        substitute(module("de.jensklingenberg.ktorfit:converters-call"))
            .using(project(":ktorfit-converters:call")).because("we work with the unreleased development version")
    }
}

