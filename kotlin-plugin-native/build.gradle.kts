import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

val kspVersion: String by project
val ktorfitVersion: String by project
val autoService: String by project
val kotlinPoet: String by project
val detektVersion: String by project

plugins {
    kotlin("jvm")
    id("com.vanniktech.maven.publish")
    `maven-publish`
    signing
    id("org.jetbrains.dokka")
    id("io.gitlab.arturbosch.detekt").version("1.21.0")
    kotlin("kapt")
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}



val enableSigning: String by project

mavenPublishing {
    publishToMavenCentral()
    // publishToMavenCentral(SonatypeHost.S01) for publishing through s01.oss.sonatype.org
    if(enableSigning.toBoolean()){
        signAllPublications()
    }
}

group = "de.jensklingenberg.ktorfit"
version = ktorfitVersion

dependencies {

    testImplementation("junit:junit:4.13.2")
    testImplementation("com.google.truth:truth:1.1.3")
    compileOnly ("com.google.auto.service:auto-service:$autoService")
    kapt ("com.google.auto.service:auto-service:$autoService")
    testImplementation("junit:junit:4.13.2")
    testImplementation ("org.mockito.kotlin:mockito-kotlin:4.0.0")
    compileOnly("org.jetbrains.kotlin:kotlin-compiler:1.8.0")


}


detekt {
    toolVersion = "1.21.0"
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

tasks.withType<KotlinCompilationTask<*>>().configureEach {
    compilerOptions.freeCompilerArgs.add("-opt-in=org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi")
}