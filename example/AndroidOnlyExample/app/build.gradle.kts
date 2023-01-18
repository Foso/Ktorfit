plugins {
    id ("com.android.application")
    id ("org.jetbrains.kotlin.android")
    id ("com.google.devtools.ksp") version "1.7.20-1.0.8"
    id ("org.jetbrains.kotlin.plugin.serialization") version "1.7.21"

}
apply(plugin = "de.jensklingenberg.ktorfit.gradle")

android {
    namespace= ("de.jensklingenberg.androidonlyexample")
    compileSdk= (33)

    defaultConfig {
        applicationId= ("de.jensklingenberg.androidonlyexample")
        minSdk= (21)
        targetSdk =(33)
        versionCode= (1)
        versionName= ("1.0")

        testInstrumentationRunner= ("androidx.test.runner.AndroidJUnitRunner")
        vectorDrawables {
            useSupportLibrary= true
        }
    }

    buildTypes {
        release {
            //minifyEnabled=( false)
          //  proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    compileOptions {
        sourceCompatibility (JavaVersion.VERSION_1_8)
        targetCompatibility (JavaVersion.VERSION_1_8)
    }
    kotlinOptions {
        //jvmTarget  '1.8'
    }
    buildFeatures {
        compose= (true)
    }
    composeOptions {
        kotlinCompilerExtensionVersion= ("1.3.2")
    }
    packagingOptions {
        resources {

        }
    }
}
val ktorfit = "1.0.0-beta17"
val ktor = "2.1.3"
val compose_ui_version = "1.3.2"
dependencies {
    ksp("de.jensklingenberg.ktorfit:ktorfit-ksp:$ktorfit")
    implementation("de.jensklingenberg.ktorfit:ktorfit-lib:$ktorfit")
    implementation ("androidx.core:core-ktx:1.9.0")
    implementation ("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation ("androidx.activity:activity-compose:1.6.1")
    implementation ("androidx.compose.ui:ui:$compose_ui_version")
    implementation ("androidx.compose.ui:ui-tooling-preview:$compose_ui_version")
    implementation ("androidx.compose.material:material:1.3.1")
    testImplementation ("junit:junit:4.13.2")
    androidTestImplementation ("androidx.test.ext:junit:1.1.4")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.0")
    androidTestImplementation ("androidx.compose.ui:ui-test-junit4:$compose_ui_version")
    debugImplementation ("androidx.compose.ui:ui-tooling:$compose_ui_version")
    debugImplementation ("androidx.compose.ui:ui-test-manifest:$compose_ui_version")
    implementation("io.ktor:ktor-client-serialization:$ktor")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor")
}
