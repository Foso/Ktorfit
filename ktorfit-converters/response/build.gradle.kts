import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    id("maven-publish")
    id("signing")
    id("com.vanniktech.maven.publish")
    id("com.android.library")
    alias(libs.plugins.binaryCompatibilityValidator)
    id("app.cash.licensee")
    id("org.jlleitschuh.gradle.ktlint")
}

licensee {
    allow("Apache-2.0")
    allow("MIT")
}

val enableSigning = project.hasProperty("signingInMemoryKey")

mavenPublishing {
    val artifactId =
        "ktorfit-converters-response" +
            if (libs.versions.ktorVersion
                    .get()
                    .startsWith("3.")
            ) {
                "-ktor-" + libs.versions.ktorVersion.get()
            } else {
                ""
            }
    coordinates(
        libs.versions.groupId.get(),
        artifactId,
        libs.versions.ktorfitResponseConverter.get(),
    )
    publishToMavenCentral()
    // publishToMavenCentral(SonatypeHost.S01) for publishing through s01.oss.sonatype.org
    if (enableSigning) {
        signAllPublications()
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

kotlin {
    explicitApi()
    if (libs.versions.ktorVersion
            .get()
            .startsWith("3.")
    ) {
        @OptIn(ExperimentalWasmDsl::class)
        wasmJs()
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
    iosArm64()
    iosX64()
    iosSimulatorArm64()
    watchosArm32()
    watchosArm64()
    watchosX64()
    watchosSimulatorArm64()
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
        val commonMain by getting {
            dependencies {
                implementation(projects.ktorfitLibCore)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        val jvmMain by getting
        val jvmTest by getting {
            dependencies {
                implementation(libs.ktor.client.mock)
                implementation(libs.mockk)
                implementation(libs.mockito.kotlin)
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val jsMain by getting
    }
}
val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

android {
    compileSdk = 34
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        targetSdk = 34
    }
    namespace = "de.jensklingenberg.ktorfit.converters.call"
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
                description.set("Response Converter for Ktorfit")
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

rootProject.plugins.withType(NodeJsRootPlugin::class) {
    rootProject.the(NodeJsRootExtension::class).version = "18.0.0"
}
