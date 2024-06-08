import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin

plugins {
    kotlin("multiplatform")
    alias(libs.plugins.kspPlugin)
    id("maven-publish")
    id("signing")
    id("com.vanniktech.maven.publish")
    id("com.android.library")
    alias(libs.plugins.detekt)
    alias(libs.plugins.binaryCompatibilityValidator)
    id("app.cash.licensee")

}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

licensee {
    allow("Apache-2.0")
    allow("MIT")
}

detekt {
    toolVersion = libs.versions.detekt.get()
    config = files("../detekt-config.yml")
    buildUponDefaultConfig = false
}

val enableSigning = project.hasProperty("signingInMemoryKey")

mavenPublishing {

    coordinates(
        "de.jensklingenberg.ktorfit",
        "ktorfit-lib-light",
        libs.versions.ktorfit.get()
    )
    publishToMavenCentral()
    //  publishToMavenCentral(SonatypeHost.S01) // for publishing through s01.oss.sonatype.org
    if (enableSigning) {
        signAllPublications()
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

kotlin {
    if(libs.versions.ktorVersion.get().startsWith("3.")){
        @OptIn(ExperimentalWasmDsl::class)
        wasmJs()
    }
    explicitApi()
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
        tvosSimulatorArm64()
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
                api(projects.ktorfitAnnotations)
                api(libs.ktor.client.core)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        val jvmTest by getting {
            dependencies {
                kotlin.srcDir("build/generated/ksp/jvm/jvmTest/")

                dependsOn(jvmMain.get())

                implementation(libs.ktor.client.mock)
                implementation(libs.junit)
                implementation(libs.mockito.kotlin)
                implementation(libs.ktor.client.cio.jvm)
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting

        val iosMain by getting {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {}
        }
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
    namespace = "de.jensklingenberg.ktorfit.common"
}



publishing {
    publications {
        create<MavenPublication>("default") {
            artifact(tasks["sourcesJar"])
            // artifact(tasks["javadocJar"])

            pom {
                name.set(project.name)
                description.set("Ktorfit Client Library (light)")
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
                val url = when {
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

ksp {
    arg("Ktorfit_QualifiedTypeName", "true")
}

rootProject.plugins.withType(NodeJsRootPlugin::class) {
    rootProject.the(NodeJsRootExtension::class).version = "18.0.0"
}

dependencies {
    add(
        "kspCommonMainMetadata", projects.ktorfitKsp
    )
    add("kspJvm", projects.ktorfitKsp)
    add("kspJvmTest", projects.ktorfitKsp)
}
