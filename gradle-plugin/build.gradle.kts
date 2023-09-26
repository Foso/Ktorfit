plugins {
    kotlin("jvm") version("1.9.10")
    kotlin("kapt") version("1.9.10")
    id("java-gradle-plugin")
    `maven-publish`
    `kotlin-dsl`
    id("com.gradle.plugin-publish") version "1.2.1"
}

group = "de.jensklingenberg.ktorfit"
version = "1.8.0"


allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://maven.google.com")
        maven("https://plugins.gradle.org/m2/")
        google()
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

dependencies {
    add("compileOnly", kotlin("gradle-plugin"))
    add("compileOnly", kotlin("gradle-plugin-api"))
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin-api:1.9.10")
}

gradlePlugin {
    website.set("https://github.com/Foso/Ktorfit")
    vcsUrl.set("https://github.com/Foso/Ktorfit")
    plugins {

        create("ktorfitPlugin") {
            id = "de.jensklingenberg.ktorfit" // users will do `apply plugin: "de.jensklingenberg.ktorfit"`
            implementationClass = "de.jensklingenberg.ktorfit.gradle.KtorfitGradlePlugin" // entry-point class
            displayName = "Ktorfit Gradle Plugin"
            description = "Gradle Plugin for Ktorfit"
            tags.set(listOf("http","kotlin","kotlin-mpp","ktor","rest"))
        }
    }
}



publishing {
    publications {
        create<MavenPublication>("default") {
            from(components["java"])


            pom {
                name.set("ktorfit-gradle-plugin")
                description.set("Gradle plugin for Ktorfit")
                url.set("https://github.com/Foso/KotlinCompilerPluginExample")

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