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
    arg("Ktorfit_QualifiedTypeName", "false")
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

