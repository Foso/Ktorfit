plugins {
    kotlin("multiplatform") apply false
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
        classpath(libs.android.build.gradle)
        classpath (libs.licensee.gradle.plugin)
        classpath("de.jensklingenberg.ktorfit:gradle-plugin:1.0.0")
    }
}

subprojects {
    repositories {
        google()
        mavenLocal()
        mavenCentral()

    }

}

// ./gradlew clean :sandbox:compileKotlinJvm --no-daemon -Dorg.gradle.debug=true -Dkotlin.compiler.execution.strategy="in-process" -Dkotlin.daemon.jvm.options="-Xdebug,-Xrunjdwp:transport=dt_socket,address=5005,server=y,suspend=n"