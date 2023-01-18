package de.jensklingenberg.ktorfit

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.types.*

@AutoService(ComponentRegistrar::class)
class CommonCompilerPluginRegistrar : ComponentRegistrar {

    override val supportsK2: Boolean
        get() = true

    override fun registerProjectComponents(project: MockProject, configuration: CompilerConfiguration) {
        if (configuration[KEY_ENABLED] == false) {
            return
        }
        val logging = configuration[KEY_LOGGING] ?: false
        val messageCollector = configuration.get(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)

        IrGenerationExtension.registerExtension(
            project,
            KtorfitIrGenerationExtension(DebugLogger(logging, messageCollector))
        )
    }
}


