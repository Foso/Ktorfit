plugins {
    id("com.vanniktech.maven.publish") version libs.versions.vannikMavenPublish.get() apply false
}

buildscript {
    repositories {
        google()
        gradlePluginPortal()
        mavenLocal()
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
        }
    }
    dependencies {
        classpath(libs.gradle.maven.publish.plugin)
        classpath(libs.dokka.gradle.plugin)
        classpath(libs.kotlin.serialization)
        classpath(libs.gradle.plugin)
    }
}

subprojects {
    repositories {
        google()
        mavenLocal()
        mavenCentral()
    }

    apply(plugin = "ktorfit.licensee")
}

// ./gradlew clean :sandbox:compileKotlinJvm --no-daemon -Dorg.gradle.debug=true -Dkotlin.compiler.execution.strategy="in-process" -Dkotlin.daemon.jvm.options="-Xdebug,-Xrunjdwp:transport=dt_socket,address=5005,server=y,suspend=n"