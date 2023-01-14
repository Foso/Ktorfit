package de.jensklingenberg

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
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
    private val pluginContext: IrPluginContext
) :
    IrElementTransformerVoidWithContext() {
    private val CREATE_FUNCTION_NAME = "create"
    private val KTORFIT_CREATE_PACKAGE = "de.jensklingenberg.ktorfit.Ktorfit"

    /**
     * Transform exampleKtorfit.create<TestApi>() to exampleKtorfit.create<TestApi>(_TestApiImpl())
     */
    override fun visitExpression(expression: IrExpression): IrExpression {

        //Find exampleKtorfit.create<TestApi>()
        (expression as? IrCall)?.let { irCall ->
            if (irCall.typeArgumentsCount > 0) {

                if (expression.symbol.owner.name.asString() != CREATE_FUNCTION_NAME) {
                    return expression
                }

                if (!expression.symbol.owner.symbol.toString().contains(KTORFIT_CREATE_PACKAGE)) {
                    return expression
                }

                //Get T from create<T>()
                val argumentType = irCall.getTypeArgument(0) ?: return expression


                val typeClassPackage = argumentType.classFqName?.asString()?.substringBeforeLast(".")!!
                val typeClassName = argumentType.classFqName?.asString()?.substringAfterLast(".") ?: ""

                //Find the class _TestApiImpl
                val implClassSymbol = pluginContext.referenceClass(
                    ClassId(
                        FqName(typeClassPackage),
                        Name.identifier("_$typeClassName" + "Impl")
                    )
                ) ?: throw NullPointerException("_$typeClassName" + "Impl not found, did you apply the KSP Ktorfit plugin?")

                val newConstructor = implClassSymbol.constructors.first()

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