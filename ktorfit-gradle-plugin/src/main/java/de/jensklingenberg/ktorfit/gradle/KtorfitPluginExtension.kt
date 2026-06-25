package de.jensklingenberg.ktorfit.gradle

import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

/**
 * Configure the ktorfit gradle plugin to match your needs.
 */
open class KtorfitPluginExtension
    @Inject
    constructor(
        objectFactory: ObjectFactory
    ) {
        /**
         * Set whether qualified type names should be generated or not.
         *
         * Default: false
         */
        open val generateQualifiedTypeName: Property<Boolean> = objectFactory.property(Boolean::class.java)

        /**
         * Specify the Ktorfit error checking mode.
         *
         * Default: [ErrorCheckingMode.ERROR]
         * @see ErrorCheckingMode
         */
        open val errorCheckingMode: Property<ErrorCheckingMode> = objectFactory.property(ErrorCheckingMode::class.java)

        /**
         * Specify the version of the Ktorfit compiler plugin
         *
         * Default: [KtorfitGradlePlugin.KTORFIT_COMPILER_PLUGIN_VERSION]
         * Set value to "-" to disable the compiler plugin
         * @see [Compatibility-table](https://github.com/Foso/Ktorfit/tree/master/ktorfit-compiler-plugin#compatibility-table)
         */
        open val compilerPluginVersion: Property<String> = objectFactory.property(String::class.java)

        /**
         * Specify the Kotlin version of the compiler plugin.
         *
         * Default: your current Kotlin version
         * Set value to "-" to disable the compiler plugin
         */
        @Deprecated("Use compilerPluginVersion instead", ReplaceWith("compilerPluginVersion"))
        open val kotlinVersion: Property<String> = compilerPluginVersion

        /**
         * Enables per-target KSP code generation instead of relying on
         * `kspCommonMainKotlinMetadata` for shared code.
         *
         * When enabled, each KMP target runs KSP independently and generates
         * its own copy of the implementation code. This removes the serial
         * bottleneck of the metadata task triggering metadata compilation for all
         * targets from child modules, but can make usage of the typesafe creation api more difficult.
         *
         * Consider also using [enableFactoryRegistry] to also enable Ktorfit.createUsingRegistry for simplified use
         *
         * Default: false (uses the traditional metadata-based approach)
         */
        open val perTargetGeneration: Property<Boolean> = objectFactory.property(Boolean::class.java)

        /**
         * Enables the self-registering factory registry for API implementations.
         *
         * When enabled, each generated `_<Name>Impl.kt` file includes a module-level
         * property that registers its [ClassProvider] into [KtorfitFactoryRegistry].
         * This allows using `Ktorfit.createUsingRegistry<T>()` from commonMain without
         * relying on the compiler plugin.
         *
         * This flag is independent of [perTargetGeneration] but works well with it:
         * - With [perTargetGeneration] = true: each target registers its own factory
         * - With [perTargetGeneration] = false: only the metadata compilation registers factories
         *
         * Default: false
         * @see de.jensklingenberg.ktorfit.optins.KtorfitFactoryRegistry
         * @see de.jensklingenberg.ktorfit.ExperimentalFactoryRegistry
         */
        open val enableFactoryRegistry: Property<Boolean> = objectFactory.property(Boolean::class.java)

        internal fun setupConvention(project: Project) {
            generateQualifiedTypeName.convention(false)
            errorCheckingMode.convention(ErrorCheckingMode.ERROR)
            perTargetGeneration.convention(false)
            enableFactoryRegistry.convention(false)
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
