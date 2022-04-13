plugins {
    kotlin("multiplatform") apply false
    id("com.vanniktech.maven.publish") version "0.18.0" apply false

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
    classpath("com.vanniktech:gradle-maven-publish-plugin:0.18.0")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:1.5.30")
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.6.20")
        classpath("com.android.tools.build:gradle:7.0.4")


    }
}

subprojects {
    repositories {
        google()
        mavenLocal()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
        }
    }

}

