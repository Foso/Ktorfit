buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenLocal()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.0")
        classpath("com.android.tools.build:gradle:7.2.2")
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.8.0")
        classpath("de.jensklingenberg.ktorfit:gradle-plugin:1.0.0-beta17")

    }
}

allprojects {
    repositories {
        google()
        mavenLocal()
        mavenCentral()
    }
}
