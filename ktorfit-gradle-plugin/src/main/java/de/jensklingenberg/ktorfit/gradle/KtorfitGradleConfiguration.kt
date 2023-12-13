package de.jensklingenberg.ktorfit.gradle

open class KtorfitGradleConfiguration {
    /**
     * If the compiler plugin should be active
     */
    internal var enabled: Boolean = true

    /**
     * version number of the compiler plugin
     */
    internal var version: String = "2.0.0-SNAPSHOT" // remember to bump this version before any release!

    /**
     * used to get debug information from the compiler plugin
     */
    var logging: Boolean = false
}