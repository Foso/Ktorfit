package de.jensklingenberg.ktorfit.gradle

open class KtorfitGradleConfiguration {


    /**
     * used to get debug information from the compiler plugin
     */
    var logging: Boolean = false

    var addKspDependencies : Boolean = true
}