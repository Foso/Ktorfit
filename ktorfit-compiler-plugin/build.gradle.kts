import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven
import org.gradle.plugins.signing.Sign


val enableSigning = project.hasProperty("signingInMemoryKey")

plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("com.vanniktech.maven.publish")
    signing
    alias(libs.plugins.detekt)
    id("app.cash.licensee")
    id("org.jlleitschuh.gradle.ktlint")
}

licensee {
    allow("Apache-2.0")
    allow("MIT")
}

mavenPublishing {
    coordinates(libs.versions.groupId.get(), "compiler-plugin", libs.versions.ktorfitCompiler.get())
    publishToMavenCentral()
    if (enableSigning) {
        signAllPublications()
    }
}

// Fix task dependencies for signing and publishing
if (enableSigning) {
    tasks.withType<AbstractPublishToMaven>().configureEach {
        dependsOn(tasks.withType<Sign>())
    }
}

dependencies {
    compileOnly(libs.autoService)
    kapt(libs.autoService)
    compileOnly(libs.kotlin.compiler.embeddable)
    testImplementation(libs.kctfork.core)
    testImplementation(libs.junit)
    testImplementation(kotlin("reflect"))
}

detekt {
    toolVersion = libs.versions.detekt.get()
    config.from(files("../detekt-config.yml"))
    buildUponDefaultConfig = false
}

publishing {
    publications {
        create<MavenPublication>("default") {
            from(components["java"])

            pom {
                name.set("ktorfit-compiler-plugin")
                description.set("Compiler Plugin for Ktorfit")
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

tasks.withType<KotlinCompile> {
    compilerOptions.jvmTarget = JvmTarget.JVM_1_8
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
