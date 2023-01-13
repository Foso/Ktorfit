package de.jensklingenberg

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.jvm.ir.getKtFile
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.config.addKotlinSourceRoot
import org.jetbrains.kotlin.cli.common.config.kotlinSourceRoots
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.createSourceFilesFromSourceRoots
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.com.intellij.openapi.extensions.LoadingOrder
import org.jetbrains.kotlin.com.intellij.openapi.vfs.local.CoreLocalFileSystem
import org.jetbrains.kotlin.com.intellij.openapi.vfs.local.CoreLocalVirtualFile
import org.jetbrains.kotlin.com.intellij.psi.PsiManager
import org.jetbrains.kotlin.com.intellij.psi.SingleRootFileViewProvider
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey
import org.jetbrains.kotlin.config.JVMConfigurationKeys
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.backend.js.ic.KotlinSourceFile
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.declarations.impl.IrClassImpl
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.expressions.impl.IrConstructorCallImpl
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.extensions.AnalysisHandlerExtension
import org.jetbrains.kotlin.types.*
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstance
import java.io.File
import java.nio.file.Files

val OUTPUT_DIRECTORY = CompilerConfigurationKey.create<File>("output directory")

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
        val output = configuration[JVMConfigurationKeys.OUTPUT_DIRECTORY]

           // configuration.addKotlinSourceRoot(secTemp.toAbsolutePath().toString() + "/temp.kt", true)

        AnalysisHandlerExtension.registerExtension(project, CodeGenerationExtension(messageCollector, output,configuration))

        project.extensionArea.getExtensionPoint(IrGenerationExtension.extensionPointName).registerExtension(RedactedIrGenerationExtension(messageCollector), LoadingOrder.LAST, project)

    }
}

fun createNewKtFile(
    name: String, content: String, outputDir: String, fileManager: PsiManager
): KtFile {
    val directory = File(outputDir).apply { mkdirs() }
    val file = File(directory, name).apply { writeText(content) }
    val virtualFile = CoreLocalVirtualFile(CoreLocalFileSystem(), file)
    return KtFile(SingleRootFileViewProvider(fileManager, virtualFile), false)
}


class RedactedIrGenerationExtension(val messageCollector: MessageCollector) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        moduleFragment.transform(ElementTransformer(moduleFragment, messageCollector, pluginContext), null)
    }
}

class ElementTransformer(
    private val moduleFragment: IrModuleFragment,
    val messageCollector: MessageCollector,
    val pluginContext: IrPluginContext
) :
    IrElementTransformerVoidWithContext() {

    override fun visitValueParameterNew(declaration: IrValueParameter): IrStatement {
        declaration.transform(CreateFuncTransformer(moduleFragment, messageCollector, pluginContext), null)
        return super.visitValueParameterNew(declaration)
    }

    override fun visitPropertyNew(declaration: IrProperty): IrStatement {
        declaration.transform(CreateFuncTransformer(moduleFragment, messageCollector, pluginContext), null)
        return super.visitPropertyNew(declaration)
    }

    override fun visitCall(expression: IrCall): IrExpression {
        expression.transform(CreateFuncTransformer(moduleFragment, messageCollector, pluginContext), null)

        return super.visitCall(expression)
    }

    override fun visitVariable(declaration: IrVariable): IrStatement {
        declaration.transform(CreateFuncTransformer(moduleFragment, messageCollector, pluginContext), null)
        return super.visitVariable(declaration)
    }

    override fun visitSimpleFunction(declaration: IrSimpleFunction): IrStatement {
        if (declaration.parent.kotlinFqName.asString() == "main") {

            val dec = declaration
            return super.visitSimpleFunction(dec)
        }
        return super.visitSimpleFunction(declaration)
    }

    override fun visitFunctionExpression(expression: IrFunctionExpression): IrExpression {
        expression.transform(CreateFuncTransformer(moduleFragment, messageCollector, pluginContext), null)

        return super.visitFunctionExpression(expression)
    }


}

private fun TypeConstructor.makeNonNullType() =
    KotlinTypeFactory.simpleType(TypeAttributes.Empty, this, listOf(), false)

class CreateFuncTransformer(
    private val moduleFragment: IrModuleFragment,
    val messageCollector: MessageCollector,
    val pluginContext: IrPluginContext
) :
    IrElementTransformerVoidWithContext() {

    companion object {
        var files: MutableList<IrFile> = mutableListOf()
    }


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
                KotlinSourceFile("")
                if (!expression.symbol.owner.symbol.toString().contains("de.jensklingenberg.ktorfit.Ktorfit")) {
                    return expression
                }

                if (files.isEmpty()) {
                    files.addAll(moduleFragment.files)
                }
               // moduleFragment.files.first() { it.name.contains("temp") }.transform(Transi(), null)


                //val kt = createNewKtFile("test","","",expression.psiElement!!.manager)


                //Get T from create<T>()
                val argumentType = irCall.getTypeArgument(0) ?: return expression



                val className = argumentType.classFqName?.asString()?.substringAfterLast(".") ?: ""
                val implClassName = "_$className" + "Impl.kt"

                //Find the class _TestApiImpl.kt
                val classT =
                    files.firstOrNull { it.name == implClassName }?.declarations?.firstIsInstance<IrClassImpl>()


                // IrClassImpl(0,0,IrDeclarationOrigin.DEFINED,ClassKind.CLASS,)
                val implClassSymbol = if (classT == null) {
                    pluginContext.referenceClass(
                        ClassId(
                            FqName("com.example.api"),
                            Name.identifier("_JsonPlaceHolderApiImpl")
                        )
                    ) ?: throw NullPointerException()
                } else {
                    classT.symbol ?: moduleFragment.irBuiltins.anyClass
                }

              //  moduleFragment.files.removeIf { it.getKtFile()!!.virtualFilePath.contains("temp") }

                val newConstructor = implClassSymbol!!.constructors.first()

                //Create the constructor call for _ExampleApiImpl()
                val newCall = IrConstructorCallImpl(
                    0,
                    0,
                    type = implClassSymbol.defaultType,
                    symbol = newConstructor,
                    0,
                    0,
                    0,
                    null
                )

                //Set _ExampleApiImpl() as argument for create<ExampleApi>()
                irCall.putValueArgument(0, newCall)
                return super.visitExpression(irCall)
            }
        }
        return super.visitExpression(expression)
    }


}

class Transi : IrElementTransformerVoidWithContext() {

    override fun visitPackageFragment(declaration: IrPackageFragment): IrPackageFragment {
        return super.visitPackageFragment(declaration)
    }

    override fun visitClassReference(expression: IrClassReference): IrExpression {
        return super.visitClassReference(expression)
    }

    override fun visitConstructor(declaration: IrConstructor): IrStatement {

        return super.visitConstructor(declaration)
    }

    override fun visitConstructorCall(expression: IrConstructorCall): IrExpression {
        return super.visitConstructorCall(expression)
    }
}


