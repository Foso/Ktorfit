package de.jensklingenberg.ktorfit

class KtorfitOptions(
    options: Map<String, String>
) {
    /**
     * 0: Turn off all Ktorfit related error checking
     *
     * 1: Check for errors
     *
     * 2: Turn errors into warnings
     */
    val errorsLoggingType: Int = (options["Ktorfit_Errors"]?.toIntOrNull()) ?: 1

    /**
     * If set to true, the generated code will contain qualified type names
     */
    val setQualifiedType = options["Ktorfit_QualifiedTypeName"]?.toBoolean() ?: false

    /**
     * If the compilation is multiplatform and has only one target, this will be true
     */
    val multiplatformWithSingleTarget = options["Ktorfit_MultiplatformWithSingleTarget"]?.toBoolean() ?: false
}
