package de.jensklingenberg.ktorfit.gradle

open class KtorfitGradleConfiguration {
    /**
     * If the compiler plugin should be active
     */
    var enabled: Boolean = true

    /**
     * version number of the compiler plugin
     */
    @Deprecated("Update the this Gradle plugin instead of updating this version")
    var version: String = "1.7.0" // remember to bump this version before any release!

    /**
     * used to get debug information from the compiler plugin
     */
    var logging: Boolean = false
}