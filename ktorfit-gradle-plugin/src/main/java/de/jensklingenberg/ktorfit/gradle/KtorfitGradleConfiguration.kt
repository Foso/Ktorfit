package de.jensklingenberg.ktorfit.gradle

open class KtorfitGradleConfiguration {
    var generateQualifiedTypeName: Boolean = false
    var errorCheckingMode: ErrorCheckingMode = ErrorCheckingMode.ERROR
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
