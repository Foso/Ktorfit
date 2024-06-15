package de.jensklingenberg.ktorfit

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment

internal class KtorfitIrGenerationExtension(private val debugLogger: DebugLogger) : IrGenerationExtension {
    override fun generate(
        moduleFragment: IrModuleFragment,
        pluginContext: IrPluginContext,
    ) {
        moduleFragment.transform(ElementTransformer(pluginContext, debugLogger), null)
    }
}
