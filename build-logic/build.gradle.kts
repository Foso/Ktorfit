plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.android.build.gradle)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.gradle.maven.publish.plugin)
    compileOnly(libs.licensee.gradle.plugin)
    compileOnly(libs.detekt.gradle.plugin)
}
