plugins {
    id("ktorfit.kmp")
    alias(libs.plugins.kspPlugin)
    id("maven-publish")
    id("signing")
    id("com.vanniktech.maven.publish")
    alias(libs.plugins.detekt)
    id("app.cash.licensee")
    id("org.jlleitschuh.gradle.ktlint")
}

ktlint {
    filter {
        exclude {
            it.file.path.contains(
                layout.buildDirectory
                    .dir("generated")
                    .get()
                    .toString(),
            )
        }
    }
}

licensee {
    allow("Apache-2.0")
    allow("MIT")
    allowUrl("https://opensource.org/license/mit")
}

detekt {
    toolVersion = libs.versions.detekt.get()
    config.from(files("../detekt-config.yml"))
    buildUponDefaultConfig = false
}

val enableSigning = project.hasProperty("signingInMemoryKey")

// Fix task dependencies for signing and publishing
if (enableSigning) {
    tasks.withType<AbstractPublishToMaven>().configureEach {
        dependsOn(tasks.withType<Sign>())
    }
}

mavenPublishing {

    coordinates(
        version = libs.versions.ktorfit.get(),
    )
    publishToMavenCentral()
    if (enableSigning) {
        signAllPublications()
    }
}

kotlin {
    explicitApi()

    sourceSets {
        commonMain {
            dependencies {
                api(projects.ktorfitAnnotations)
                api(libs.ktor.client.core)
            }
        }
        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        jvmTest {
            dependencies {
                kotlin.srcDir("build/generated/ksp/jvm/jvmTest/")

                implementation(libs.ktor.client.mock)
                implementation(libs.junit)
                implementation(libs.mockito.kotlin)
                implementation(libs.ktor.client.cio.jvm)
                implementation(libs.kotlin.coroutines.test)
            }
        }
    }
}

tasks.register<Jar>("javadocJar").configure {
    archiveClassifier.set("javadoc")
}

android {
    namespace = "de.jensklingenberg.ktorfit.common"
    defaultConfig {
        val proguardFile =
            file("src/jvmMain/resources/META-INF/proguard/ktorfit.pro").also {
                if (!it.exists()) {
                    throw NoSuchFileException(
                        file = it,
                        reason = "We have to provide a proguard rules file for the library.",
                    )
                }
            }
        consumerProguardFiles(proguardFile)
    }
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

ksp {
    arg("Ktorfit_QualifiedTypeName", "true")
}

dependencies {
    add(
        "kspCommonMainMetadata",
        projects.ktorfitKsp,
    )
    add("kspJvm", projects.ktorfitKsp)
    add("kspJvmTest", projects.ktorfitKsp)
}
