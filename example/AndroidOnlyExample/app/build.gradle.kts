plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp") version "2.1.10-1.0.31"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.0"
    id("de.jensklingenberg.ktorfit") version "2.5.0"
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.10"
}

ktorfit{

}

android {
    namespace = "de.jensklingenberg.androidonlyexample"
    compileSdk = 35

    defaultConfig {
        applicationId = ("de.jensklingenberg.androidonlyexample")
        minSdk = 21
        targetSdk = 35
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

val ktorfitVersion = "2.5.0"
val ktor = "3.1.2"
val compose_ui_version = "1.7.8"
dependencies {
    implementation("de.jensklingenberg.ktorfit:ktorfit-lib:$ktorfitVersion")
    implementation("io.ktor:ktor-client-serialization:$ktor")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor")
    implementation("de.jensklingenberg.ktorfit:ktorfit-converters-response:$ktorfitVersion")
    implementation("de.jensklingenberg.ktorfit:ktorfit-converters-call:$ktorfitVersion")
    implementation("de.jensklingenberg.ktorfit:ktorfit-converters-flow:$ktorfitVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.compose.ui:ui:$compose_ui_version")
    implementation("androidx.compose.ui:ui-tooling-preview:$compose_ui_version")
    implementation("androidx.compose.material:material:1.7.8")
    debugImplementation("androidx.compose.ui:ui-tooling:$compose_ui_version")
    debugImplementation("androidx.compose.ui:ui-test-manifest:$compose_ui_version")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$compose_ui_version")
}

