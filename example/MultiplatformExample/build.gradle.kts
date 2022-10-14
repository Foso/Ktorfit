buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenLocal()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.0")
        classpath("com.android.tools.build:gradle:7.2.2")
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.7.20")

    }
}

allprojects {
    repositories {
        google()
        mavenLocal()
        mavenCentral()
    }
}
