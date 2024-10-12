buildscript {
    repositories {
        // mavenLocal()
        gradlePluginPortal()
        google()
        mavenCentral()
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots")
        }
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.21")
        classpath("com.android.tools.build:gradle:8.5.2")
        classpath("org.jetbrains.kotlin:kotlin-serialization:2.0.21")
    }
}

allprojects {
    repositories {
        // mavenLocal()
        google()
        mavenCentral()
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots")
        }
    }
}
