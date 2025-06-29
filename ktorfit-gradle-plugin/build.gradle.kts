import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("java-gradle-plugin")
    `kotlin-dsl`
    id("com.gradle.plugin-publish") version "1.3.1"
    id("com.vanniktech.maven.publish")
    id("org.jlleitschuh.gradle.ktlint")
}

dependencies {
    add("compileOnly", kotlin("gradle-plugin"))
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions.jvmTarget = JvmTarget.JVM_1_8
}

dependencies {
    implementation(libs.kotlin.gradle.plugin.api)
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
            tags.set(listOf("http", "kotlin", "kotlin-mpp", "ktor", "rest"))
        }
    }
}

val enableSigning = project.hasProperty("signingInMemoryKey")

mavenPublishing {

    coordinates(
        libs.versions.groupId.get(),
        "ktorfit-gradle-plugin",
        libs.versions.ktorfitGradle.get(),
    )
    publishToMavenCentral()
    if (enableSigning) {
        signAllPublications()
    }
}

publishing {
    publications {
        create<MavenPublication>("default") {
            from(components["java"])

            pom {
                name.set("ktorfit-gradle-plugin")
                description.set("Gradle plugin for Ktorfit")
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
