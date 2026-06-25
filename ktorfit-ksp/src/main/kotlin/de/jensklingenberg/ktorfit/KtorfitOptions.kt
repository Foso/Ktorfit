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
     * If the compilation is multiplatform and has only one target, this will be true.
     * Only relevant in the legacy (non-per-target) code path.
     */
    val multiplatformWithSingleTarget = options["Ktorfit_MultiplatformWithSingleTarget"]?.toBoolean() ?: false

    /**
     * When true, code generation is done independently per target instead of
     * relying on `kspCommonMainKotlinMetadata`. This removes the commonMain
     * module filtering in [ClassGenerator] and allows the processor to
     * generate code for every source file visible to the current compilation.
     */
    val perTargetGeneration = options["Ktorfit_PerTargetGeneration"]?.toBoolean() ?: false

    /**
     * If set to true, the generated code will add all created class providers to central registry enabling the usage of
     * the Ktorfit.createUsingRegistry() function.
     */
    val enableFactoryRegistry = options["Ktorfit_EnableFactoryRegistry"]?.toBoolean() ?: false
}
