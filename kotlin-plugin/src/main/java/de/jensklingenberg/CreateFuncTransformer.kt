package de.jensklingenberg

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrConstructorCallImpl
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class CreateFuncTransformer(
    private val moduleFragment: IrModuleFragment,
    val messageCollector: MessageCollector,
    private val pluginContext: IrPluginContext
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


                // moduleFragment.files.first() { it.name.contains("temp") }.transform(Transi(), null)


                //val kt = createNewKtFile("test","","",expression.psiElement!!.manager)


                //Get T from create<T>()
                val argumentType = irCall.getTypeArgument(0) ?: return expression


                val packaage = argumentType.classFqName?.asString()?.substringBeforeLast(".")!!
                val className = argumentType.classFqName?.asString()?.substringAfterLast(".") ?: ""
                val implClassName = "_$className" + "Impl.kt"

                //Find the class _TestApiImpl.kt

                val implClassSymbol = pluginContext.referenceClass(
                    ClassId(
                        FqName(packaage),
                        Name.identifier("_$className" + "Impl")
                    )
                ) ?: throw NullPointerException("_$className" + "Impl not found, did you apply the Ksp Ktorfit plugin?")

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