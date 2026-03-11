plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.android.build.gradle)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.gradle.maven.publish.plugin)
    implementation(libs.licensee.gradle.plugin)
    implementation(libs.detekt.gradle.plugin)
}
