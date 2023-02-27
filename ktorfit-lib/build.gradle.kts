plugins {
    kotlin("multiplatform")
    id("kotlinx-serialization")
    id("maven-publish")
    id("signing")
    id("com.vanniktech.maven.publish")
    id("org.jetbrains.dokka")
    id("com.android.library")
    id("io.gitlab.arturbosch.detekt").version("1.22.0")
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.13.0"
    id("app.cash.licensee")
}

licensee {
    allow("Apache-2.0")
    allow("MIT")
}

detekt {
    toolVersion = "1.22.0"
    config = files("../detekt-config.yml")
    buildUponDefaultConfig = false
}
val enableSigning = project.hasProperty("ORG_GRADLE_PROJECT_signingInMemoryKey")

mavenPublishing {
    publishToMavenCentral()
    // publishToMavenCentral(SonatypeHost.S01) for publishing through s01.oss.sonatype.org
    if(enableSigning){
        signAllPublications()
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
val ktorfitVersion: String by project
val ktorVersion: String by project

version = ktorfitVersion

kotlin {
    explicitApi()
    android {
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
    mingwX64()

    js(IR) {
        this.nodejs()
        binaries.executable() // not applicable to BOTH, see details below
    }
    linuxX64 {
        binaries {
            executable()
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":ktorfit-annotations"))

                api("io.ktor:ktor-client-core:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

            }
        }
        val linuxX64Main by getting {
            dependencies {
                implementation("io.ktor:ktor-client-core-linuxx64:$ktorVersion")
                implementation("io.ktor:ktor-client-cio-linuxx64:$ktorVersion")

            }
        }

        val mingwX64Main by getting {
            dependencies {
                implementation("io.ktor:ktor-client-core-mingwx64:$ktorVersion")

            }
        }
        val androidMain by getting{

            dependencies {
                implementation("io.ktor:ktor-client-cio-jvm:$ktorVersion")

            }
        }

        val jvmMain by getting {

            dependencies {
                implementation("io.ktor:ktor-client-cio-jvm:$ktorVersion")

            }
        }
        val jvmTest by getting {

            dependencies {
                dependsOn(jvmMain)
                implementation("io.ktor:ktor-client-mock:$ktorVersion")
                implementation("junit:junit:4.13.2")
                implementation ("org.mockito.kotlin:mockito-kotlin:4.1.0")

            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val jsMain by getting {

            dependencies {
                implementation("io.ktor:ktor-client-js:$ktorVersion")
            }
        }

        val iosMain by getting{
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                implementation("io.ktor:ktor-client-ios:$ktorVersion")
            }
        }
    }
}
val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

android {
    compileSdk = 33
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        targetSdk = 33
    }
}



publishing {
    publications {
        create<MavenPublication>("default") {
            artifact(tasks["sourcesJar"])
           // artifact(tasks["javadocJar"])

            pom {
                name.set(project.name)
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

rootProject.plugins.withType(org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootPlugin::class){
    rootProject.the(org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension::class).nodeVersion = "18.0.0"
}
