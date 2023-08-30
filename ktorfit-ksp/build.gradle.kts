import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val enableSigning = project.hasProperty("ORG_GRADLE_PROJECT_signingInMemoryKey")

plugins {
    id("ktorfit.kotlinJVM")
    id("com.vanniktech.maven.publish")
    `maven-publish`
    signing
    id("org.jetbrains.dokka")
    alias(libs.plugins.detekt)
    kotlin("kapt")
}

mavenPublishing {
    coordinates("de.jensklingenberg.ktorfit", "ktorfit-ksp", libs.versions.ktorfit.asProvider().get())
    publishToMavenCentral()
    // publishToMavenCentral(SonatypeHost.S01) for publishing through s01.oss.sonatype.org
    if (enableSigning) {
        signAllPublications()
    }
}


dependencies {
    implementation(projects.ktorfitAnnotations)
    implementation(libs.kspApi)
    implementation(libs.kotlinPoet)
    implementation(libs.kotlinPoet.ksp)

    compileOnly(libs.autoService)
    kapt(libs.autoService)

    /* TEST  */
    testImplementation(libs.junit)
    testImplementation(libs.truth)
    testImplementation(libs.kctfork.core)
    testImplementation(libs.kctfork.ksp)
    testImplementation(libs.mockito.kotlin)

}

detekt {
    toolVersion = libs.versions.detekt.get()
    config = files("../detekt-config.yml")
    buildUponDefaultConfig = false
}

tasks.register("sourcesJar", Jar::class) {
    group = "build"
    description = "Assembles Kotlin sources"

    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
    dependsOn(tasks.classes)
}

publishing {
    publications {
        create<MavenPublication>("default") {
            from(components["java"])
            artifact(tasks["sourcesJar"])
            //artifact(tasks["dokkaJar"])

            pom {
                name.set(project.name)
                description.set("KSP Plugin for Ktorfit")
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