plugins {
    embeddedKotlin("jvm")
    `kotlin-dsl`
}

dependencies {
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    implementation(libs.licensee.gradlePlugin)
    implementation(libs.plugins.kotlin.multiplatform)
    implementation(libs.plugins.android.library)
}

// The Gradle Team is currently working on a way to add Gradle plugin dependencies from a version catalog to the
// buildSrc classpath. https://github.com/gradle/gradle/pull/23685
// Until then, we can use the following workaround:
fun DependencyHandler.implementation(pluginDependency: Provider<PluginDependency>): Dependency? =
    add("implementation", pluginDependency.map {
        "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version.requiredVersion}"
    }.get())