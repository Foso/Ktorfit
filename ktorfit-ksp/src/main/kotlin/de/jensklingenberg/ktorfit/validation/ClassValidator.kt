package de.jensklingenberg.ktorfit.validation

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver

/**
 * Utility object for validating that required classes exist at compilation time
 */
object ClassValidator {

    /**
     * Checks if a class exists in the classpath
     * @param resolver The KSP resolver
     * @param className Fully qualified class name
     * @return true if the class exists, false otherwise
     */
    fun classExists(resolver: Resolver, className: String): Boolean {
        return resolver.getClassDeclarationByName(
            resolver.getKSNameFromString(className)
        ) != null
    }

    /**
     * Validates that a required class exists, logging an error if it doesn't
     * @param resolver The KSP resolver
     * @param logger The KSP logger
     * @param className Fully qualified class name
     * @param errorMessage Optional custom error message
     * @return true if the class exists, false otherwise
     */
    fun validateClassExists(
        resolver: Resolver,
        logger: KSPLogger,
        className: String,
        errorMessage: String? = null
    ): Boolean {
        val exists = classExists(resolver, className)

        if (!exists) {
            val message = errorMessage
                ?: "Required class not found: $className. Make sure ktorfit-lib-core is included in your dependencies."
            logger.error(message)
        } else {
            logger.info("Validated required class exists: $className")
        }

        return exists
    }

    /**
     * Validates multiple required classes
     * @param resolver The KSP resolver
     * @param logger The KSP logger
     * @param classNames List of fully qualified class names
     * @return true if all classes exist, false if any are missing
     */
    fun validateAllClassesExist(
        resolver: Resolver,
        logger: KSPLogger,
        classNames: List<String>
    ): Boolean {
        return classNames.all { className ->
            validateClassExists(resolver, logger, className)
        }
    }
}

