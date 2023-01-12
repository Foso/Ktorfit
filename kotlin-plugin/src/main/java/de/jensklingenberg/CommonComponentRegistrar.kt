package de.jensklingenberg

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.config.kotlinSourceRoots
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.createSourceFilesFromSourceRoots
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.com.intellij.openapi.extensions.LoadingOrder
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.declarations.impl.IrClassImpl
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.expressions.impl.IrConstructorCallImpl
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstance
import java.io.File

@AutoService(ComponentRegistrar::class)
class CommonComponentRegistrar : ComponentRegistrar {

    override val supportsK2: Boolean
        get() = true

    override fun registerProjectComponents(project: MockProject, configuration: CompilerConfiguration) {
        if (configuration[KEY_ENABLED] == false) {
            return
        }

        val sourceFiles = createSourceFilesFromSourceRoots(configuration, project, configuration.kotlinSourceRoots)
//val file = File(configuration.kotlinSourceRoots.get(15).path).readText()
        val messageCollector = configuration.get(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
        configuration.kotlinSourceRoots.forEach {
            messageCollector.report(
                CompilerMessageSeverity.WARNING,
                "*** Hello from ***" + it.path
            )
        }

        project.extensionArea
            .getExtensionPoint(IrGenerationExtension.extensionPointName)
            .registerExtension( RedactedIrGenerationExtension(), LoadingOrder.LAST, project)
    }
}


class RedactedIrGenerationExtension : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        moduleFragment.transform(ElementTransformer( moduleFragment), null)
    }
}

class ElementTransformer(
    private val moduleFragment: IrModuleFragment
) :
    IrElementTransformerVoidWithContext() {

    override fun visitValueParameterNew(declaration: IrValueParameter): IrStatement {
        declaration.transform(CreateFuncTransformer( moduleFragment), null)
        return super.visitValueParameterNew(declaration)
    }

    override fun visitPropertyNew(declaration: IrProperty): IrStatement {
        declaration.transform(CreateFuncTransformer( moduleFragment), null)
        return super.visitPropertyNew(declaration)
    }

    override fun visitCall(expression: IrCall): IrExpression {
        expression.transform(CreateFuncTransformer( moduleFragment), null)

        return super.visitCall(expression)
    }
    override fun visitVariable(declaration: IrVariable): IrStatement {
        declaration.transform(CreateFuncTransformer( moduleFragment),null)
        return super.visitVariable(declaration)
    }

    override fun visitSimpleFunction(declaration: IrSimpleFunction): IrStatement {
       if(declaration.parent.kotlinFqName.asString()=="main"){
           val dec = declaration
           return super.visitSimpleFunction(dec)
       }
        return super.visitSimpleFunction(declaration)
    }

    override fun visitFunctionExpression(expression: IrFunctionExpression): IrExpression {
        expression.transform(CreateFuncTransformer( moduleFragment), null)

        return super.visitFunctionExpression(expression)
    }


}


class CreateFuncTransformer(
    private val moduleFragment: IrModuleFragment
) :
    IrElementTransformerVoidWithContext() {


    /**
     * Transform exampleKtorfit.create<TestApi>() to exampleKtorfit.create<TestApi>(_TestApiImpl())
     */
    override fun visitExpression(expression: IrExpression): IrExpression {

        //Find exampleKtorfit.create<TestApi>()
        (expression as? IrCall)?.let { irCall ->
            if (irCall.typeArgumentsCount > 0) {
                if (expression.symbol.owner.name.asString() == "runBlocking") {
                    return expression
                }
                if (expression.symbol.owner.name.asString() != "create") {
                    return expression
                }

                if (!expression.symbol.owner.symbol.toString().contains("de.jensklingenberg.ktorfit.Ktorfit")) {
                    return expression
                }

                
                //Get T from create<T>()
                val argumentType = irCall.getTypeArgument(0) ?: return expression

                val className = argumentType.classFqName?.asString()?.substringAfterLast(".") ?: ""
                val implClassName = "_$className" + "Impl.kt"

                //Find the class _TestApiImpl.kt
                val classT =
                    moduleFragment.files.firstOrNull { it.name == implClassName }?.declarations?.firstIsInstance<IrClassImpl>()
                        ?: throw NullPointerException("Class not found $className")
                val implClassSymbol = classT.symbol

                val newConstructor = implClassSymbol.constructors.first()

                //Create the constructor call for _ExampleApiImpl()
                val newCall = IrConstructorCallImpl(0, 0, type = implClassSymbol.defaultType, symbol = newConstructor, 0, 0, 0, null)

                //Set _ExampleApiImpl() as argument for create<ExampleApi>()
                irCall.putValueArgument(0, newCall)
                return super.visitExpression(irCall)
            }
        }
        return super.visitExpression(expression)
    }


}




