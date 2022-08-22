import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
val kspVersion: String by project


plugins {
    kotlin("jvm")
    id("com.vanniktech.maven.publish")
    `maven-publish`
    signing
    id("org.jetbrains.dokka")

    kotlin("kapt")
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}


group = "de.jensklingenberg.ktorfit"
version = "1.0.0-beta11"

dependencies {
   //deps.version.junit
    implementation(project(":ktorfit-annotations"))

    implementation("com.google.devtools.ksp:symbol-processing-api:$kspVersion")
    testImplementation("junit:junit:${deps.versions.junit}")
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing-ksp:${deps.versions.kotlinCompiletestingksp}")
    implementation("com.squareup:kotlinpoet:${deps.versions.kotlinPoet}")
    implementation("com.squareup:kotlinpoet-ksp:${deps.versions.kotlinPoet}")
    testImplementation("com.google.truth:truth:${deps.versions.truth}")
    compileOnly ("com.google.auto.service:auto-service:${deps.versions.autoService}")
    kapt ("com.google.auto.service:auto-service:${deps.versions.autoService}")
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
        maven {
            name = "test"
            setUrl("file://${rootProject.buildDir}/localMaven")
        }
    }
}