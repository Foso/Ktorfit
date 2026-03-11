plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.android.build.gradle)
    compileOnly(libs.kotlin.gradle.plugin)
}
