package de.jensklingenberg.ktorfit.gradle

open class KtorfitGradleConfiguration {
    /**
     * If the compiler plugin should be active
     */
    internal var enabled: Boolean = true

    /**
     * used to get debug information from the compiler plugin
     */
    var logging: Boolean = false
}