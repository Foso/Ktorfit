package de.jensklingenberg

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.config.kotlinSourceRoots
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.impl.IrClassImpl
import org.jetbrains.kotlin.ir.declarations.name
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrConstructorCallImpl
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstance

@AutoService(ComponentRegistrar::class)
class CommonComponentRegistrar : ComponentRegistrar {

    override val supportsK2: Boolean
        get() = true

    override fun registerProjectComponents(project: MockProject, configuration: CompilerConfiguration) {
        if (configuration[KEY_ENABLED] == false) {
            return
        }

        val messageCollector = configuration.get(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)

        IrGenerationExtension.registerExtension(
            project,
            RedactedIrGenerationExtension(messageCollector)
        )
    }
}


class RedactedIrGenerationExtension(val messageCollector: MessageCollector) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {

        moduleFragment.transform(Test33(moduleFragment), null)
    }
}

class Test33(
    val moduleFragment: IrModuleFragment
) :
    IrElementTransformerVoidWithContext() {

    override fun visitExpression(expression: IrExpression): IrExpression {
        (expression as? IrCall)?.let {
            if (it.typeArgumentsCount > 0) {
                if (expression.symbol.owner.name.asString() != "create") {
                    return expression
                }
            }
        }
        return expression
    }

    override fun visitCall(expression: IrCall): IrExpression {
        expression.transform(Trafo( moduleFragment), null)

        return super.visitCall(expression)
    }


}


class Trafo(
    val moduleFragment: IrModuleFragment
) :
    IrElementTransformerVoidWithContext() {


    /**
     * Transform exampleKtorfit.create<TestApi>() to exampleKtorfit.create<TestApi>(_TestApiImpl())
     */
    override fun visitExpression(expression: IrExpression): IrExpression {

        //Find exampleKtorfit.create<TestApi>()
        (expression as? IrCall)?.let {
            if (it.typeArgumentsCount > 0) {
                if (expression.symbol.owner.name.asString() != "create") {
                    return expression
                }

                if (!expression.symbol.owner.symbol.toString().contains("de.jensklingenberg.ktorfit.Ktorfit")) {
                    return expression
                }

                //Get T from create<T>
                val argumentType = it.getTypeArgument(0) ?: return expression

                val className = argumentType.classFqName?.asString()?.substringAfterLast(".") ?: ""

                //Find the class _TestApiImpl.kt
                val classT =
                    moduleFragment.files.firstOrNull() { it.name == "_$className" + "Impl.kt" }?.declarations?.firstIsInstance<IrClassImpl>()
                        ?: throw NullPointerException("Class not found $className")
                val testClass = classT?.symbol

                val newConstructor = testClass?.constructors?.first() ?: throw NullPointerException(expression.dump())

                //Create the constructor call for create<ExampleApi>(_ExampleApiImpl())
                val newCall = IrConstructorCallImpl(0, 0, type = testClass.defaultType, symbol = newConstructor, 0, 0, 0, null)

                //Set _ExampleApiImpl() as Argument for create<ExampleApi>()
                it.putValueArgument(0, newCall)
                return super.visitExpression(it)
            }
        }
        return super.visitExpression(expression)
    }


}




