plugins {
    kotlin("multiplatform") apply false
    id("com.vanniktech.maven.publish") version "0.24.0" apply false
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
        classpath("com.vanniktech:gradle-maven-publish-plugin:0.24.0")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:1.7.20")
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.8.10")
        classpath("com.android.tools.build:gradle:7.4.0-beta02")
        classpath ("app.cash.licensee:licensee-gradle-plugin:1.6.0")
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