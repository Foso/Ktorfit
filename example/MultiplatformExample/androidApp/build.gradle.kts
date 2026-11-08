plugins {
    id("com.android.application")
}

android {
    compileSdk = 36
    defaultConfig {
        applicationId = "com.example.myapplication.android"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        namespace = "com.example.myapplication.android"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8)
    }
}

dependencies {
    implementation(project(":shared"))
    implementation("com.google.android.material:material:1.14.0")
}