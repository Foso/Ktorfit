package de.jensklingenberg.ktorfit.gradle

import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

/**
 * Configure the ktorfit gradle plugin to match your needs.
 */
open class KtorfitPluginExtension(objectFactory: ObjectFactory) {
    /**
     * Set whether qualified type names should be generated or not.
     *
     * Default: false
     */
    val generateQualifiedTypeName: Property<Boolean> = objectFactory.property(Boolean::class.java)

    /**
     * Specify the Ktorfit error checking mode.
     *
     * Default: [ErrorCheckingMode.ERROR]
     * @see ErrorCheckingMode
     */
    val errorCheckingMode: Property<ErrorCheckingMode> = objectFactory.property(ErrorCheckingMode::class.java)

    /**
     * Specify the Kotlin version of the compiler plugin.
     *
     * Default: your current Kotlin version
     */
    val kotlinVersion: Property<String> = objectFactory.property(String::class.java)

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    internal fun setupConvention(project: Project) {
        generateQualifiedTypeName.convention(false)
        errorCheckingMode.convention(ErrorCheckingMode.ERROR)
        kotlinVersion.convention(project.kotlinExtension.compilerVersion)
    }
}

enum class ErrorCheckingMode {
    /**
     * Turn off all Ktorfit related error checking
     */
    NONE,

    /**
     * Check for errors
     */
    ERROR,

    /**
     * Turn errors into warnings
     */
    WARNING,
}
