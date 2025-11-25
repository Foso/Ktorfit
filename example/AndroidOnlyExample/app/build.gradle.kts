import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp") version "2.2.0-2.0.2"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.0"
    id("de.jensklingenberg.ktorfit") version "2.6.4"
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.0"
}

ktorfit{

}

android {
    namespace = "de.jensklingenberg.androidonlyexample"
    compileSdk = 36

    defaultConfig {
        applicationId = ("de.jensklingenberg.androidonlyexample")
        minSdk = 21
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            //minifyEnabled=( false)
            //  proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_1_8)
        targetCompatibility(JavaVersion.VERSION_1_8)
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = (true)
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}

val ktorfitVersion = "2.6.4"
val ktor = "3.1.2"
val compose_ui_version = "1.9.5"
dependencies {
    implementation("de.jensklingenberg.ktorfit:ktorfit-lib:$ktorfitVersion")
    implementation("io.ktor:ktor-client-serialization:$ktor")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor")
    implementation("de.jensklingenberg.ktorfit:ktorfit-converters-response:$ktorfitVersion")
    implementation("de.jensklingenberg.ktorfit:ktorfit-converters-call:$ktorfitVersion")
    implementation("de.jensklingenberg.ktorfit:ktorfit-converters-flow:$ktorfitVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.10.0")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.compose.ui:ui:$compose_ui_version")
    implementation("androidx.compose.ui:ui-tooling-preview:$compose_ui_version")
    implementation("androidx.compose.material:material:1.8.3")
    debugImplementation("androidx.compose.ui:ui-tooling:$compose_ui_version")
    debugImplementation("androidx.compose.ui:ui-test-manifest:$compose_ui_version")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$compose_ui_version")
}

