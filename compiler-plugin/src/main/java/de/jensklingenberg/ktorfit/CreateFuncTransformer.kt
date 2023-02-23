package de.jensklingenberg.ktorfit

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.descriptors.toIrBasedKotlinType
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrConstructorCallImpl
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.impl.originalKotlinType
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

/**
 * Transform exampleKtorfit.create<TestApi>() to exampleKtorfit.create<TestApi>(_TestApiImpl())
 */
class CreateFuncTransformer(
    private val pluginContext: IrPluginContext,
    private val debugLogger: DebugLogger
) :
    IrElementTransformerVoidWithContext() {

    companion object {
        fun ERROR_IMPL_NOT_FOUND(implName: String) =
            "_${implName}Impl() not found, did you apply the Ksp Ktorfit plugin?"
    }

    override fun visitExpression(expression: IrExpression): IrExpression {

        //Find exampleKtorfit.create<TestApi>()
        (expression as? IrCall)?.let { irCall ->
            if (irCall.typeArgumentsCount > 0) {
                if (expression.symbol.owner.name.asString() != "create") {
                    return expression
                }

                if (!expression.symbol.owner.symbol.toString().contains("de.jensklingenberg.ktorfit.Ktorfit")) {
                    return expression
                }

                if (expression.getValueArgument(0) != null) {
                    return expression
                }

                //Get T from create<T>()
                val argumentType = irCall.getTypeArgument(0) ?: return expression

                val classFqName = argumentType.classFqName?.asString()
                    ?: throw IllegalStateException(ERROR_IMPL_NOT_FOUND(argumentType.originalKotlinType.toString()))

                val packaage = classFqName.substringBeforeLast(".")
                val className = classFqName.substringAfterLast(".")

                //Find the class _TestApiImpl
                val implClassSymbol = pluginContext.referenceClass(
                    ClassId(
                        FqName(packaage),
                        Name.identifier("_$className" + "Impl")
                    )
                ) ?: throw IllegalStateException(ERROR_IMPL_NOT_FOUND(argumentType.originalKotlinType.toString()))

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
                debugLogger.log(
                    "Transformed " + argumentType.toIrBasedKotlinType().toString() + " to _$className" + "Impl"
                )
                return super.visitExpression(irCall)
            }
        }
        return super.visitExpression(expression)
    }

}
