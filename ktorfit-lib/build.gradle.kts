import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    id("maven-publish")
    id("signing")
    id("com.vanniktech.maven.publish")
    id("com.android.library")
    id("app.cash.licensee")
}

licensee {
    allow("Apache-2.0")
    allow("MIT")
    allowUrl("https://opensource.org/license/mit")
}

val enableSigning = project.hasProperty("signingInMemoryKey")

// Fix task dependencies for signing and publishing
if (enableSigning) {
    tasks.withType<AbstractPublishToMaven>().configureEach {
        dependsOn(tasks.withType<Sign>())
    }
}

mavenPublishing {
    val artifactId ="ktorfit-lib"
    coordinates(
        libs.versions.groupId.get(),
        artifactId,
        libs.versions.ktorfit.get(),
    )
    publishToMavenCentral()
    if (enableSigning) {
        signAllPublications()
    }
}

tasks.withType<KotlinCompile> {
    compilerOptions.jvmTarget = JvmTarget.JVM_1_8
}

kotlin {
    explicitApi()
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        nodejs()
        binaries.executable()
    }
    jvm {
    }
    js(IR) {
        this.nodejs()
        binaries.executable() // not applicable to BOTH, see details below
    }
    androidTarget {
        publishLibraryVariants("release", "debug")
    }
    androidNativeArm32()
    androidNativeArm64()
    androidNativeX86()
    androidNativeX64()

    iosArm64()
    iosX64()
    iosSimulatorArm64()
    watchosArm32()
    watchosArm64()
    watchosX64()
    watchosSimulatorArm64()
    watchosDeviceArm64()
    tvosArm64()
    tvosX64()
    tvosSimulatorArm64()
    macosX64()
    macosArm64()

    linuxX64 {
        binaries {
            executable()
        }
    }
    linuxArm64 {
        binaries {
            executable()
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
        watchosArm32(),
        watchosArm64(),
        watchosSimulatorArm64(),
        watchosDeviceArm64(),
        tvosArm64(),
        tvosX64(),
        tvosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = "library"
        }
    }
    mingwX64()
    applyDefaultHierarchyTemplate()
    sourceSets {
        commonMain {
            dependencies {
                api(projects.ktorfitLibCore)
            }
        }
        linuxX64Main {
            dependencies {
                implementation(libs.ktor.client.core.linuxX64)
                implementation(libs.ktor.client.cio.linuxX64)
            }
        }
        linuxArm64Main {
            dependencies {
                implementation(libs.ktor.client.core.linuxArm64)
                implementation(libs.ktor.client.cio.linuxArm64)
            }
        }

        mingwX64Main {
            dependencies {
                implementation(libs.ktor.client.core.mingwx64)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.ktor.client.okhttp)
            }
        }
        jvmMain {
            dependencies {
                implementation(libs.ktor.client.cio.jvm)
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

tasks.register<Jar>("javadocJar").configure {
    archiveClassifier.set("javadoc")
}

android {
    compileSdk = 34
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
    }
    namespace = "de.jensklingenberg.ktorfit"
}

publishing {
    publications {
        create<MavenPublication>("default") {
            artifact(tasks["sourcesJar"])
            // artifact(tasks["javadocJar"])

            pom {
                name.set(project.name)
                issueManagement {
                    system.set("GitHub")
                    url.set("https://github.com/Foso/Ktorfit/issues")
                }
                description.set("Ktorfit Client Library")
                url.set("https://github.com/Foso/Ktorfit")

                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("https://github.com/Foso/Ktorfit/blob/master/LICENSE.txt")
                    }
                }
                scm {
                    url.set("https://github.com/Foso/Ktorfit")
                    connection.set("scm:git:git://github.com/Foso/Ktorfit.git")
                }
                developers {
                    developer {
                        name.set("Jens Klingenberg")
                        url.set("https://github.com/Foso")
                    }
                }
            }
        }
    }

    repositories {
        if (
            hasProperty("sonatypeUsername") &&
            hasProperty("sonatypePassword") &&
            hasProperty("sonatypeSnapshotUrl") &&
            hasProperty("sonatypeReleaseUrl")
        ) {
            maven {
                val url =
                    when {
                        "SNAPSHOT" in version.toString() -> property("sonatypeSnapshotUrl")
                        else -> property("sonatypeReleaseUrl")
                    } as String
                setUrl(url)
                credentials {
                    username = property("sonatypeUsername") as String
                    password = property("sonatypePassword") as String
                }
            }
        }
    }
}
