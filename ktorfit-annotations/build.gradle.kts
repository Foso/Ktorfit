plugins {
    kotlin("multiplatform")


    id("maven-publish")
    id("signing")
    id("com.vanniktech.maven.publish")
    id("org.jetbrains.dokka")
    id("com.android.library")

}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

version = "1.0.0-beta09"

kotlin {

    android(){
        publishLibraryVariants("release", "debug")
    }
    jvm {

    }
    macosX64()
    watchosArm64()
    watchosX64()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    ios("ios") {
        binaries {
            framework {
                baseName = "library"
            }
        }
    }

    js(IR) {
        this.nodejs()
        binaries.executable() // not applicable to BOTH, see details below
    }
    linuxX64() {
        binaries {
            executable()
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {

            }
        }
        val linuxX64Main by getting {
            dependencies {

            }
        }
        val androidMain by getting{

            dependencies {

            }
        }

        val jvmMain by getting {

            dependencies {

            }
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val jsMain by getting {

            dependencies {
            }
        }

        val iosMain by getting{
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {

            }
        }
    }
}
val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

android {
    compileSdk = 32
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        targetSdk = 32
    }
}



publishing {
    publications {
        create<MavenPublication>("default") {
            artifact(tasks["sourcesJar"])
          //  artifact(tasks["javadocJar"])

            pom {
                name.set(project.name)
                description.set("Ktorfit Annotations")
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
        maven {
            name = "test"
            setUrl("file://${rootProject.buildDir}/localMaven")
        }
    }
}

rootProject.plugins.withType(org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin::class){
    rootProject.the(org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension::class).nodeVersion = "16.0.0"
}