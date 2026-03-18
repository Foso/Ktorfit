package de.jensklingenberg.ktorfit

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.descriptors.toIrBasedKotlinType
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrConstructorCallImpl
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import org.jetbrains.kotlin.ir.util.isInterface
import org.jetbrains.kotlin.js.descriptorUtils.getKotlinTypeFqName
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

/**
 * Transform exampleKtorfit.create<TestApi>() to exampleKtorfit.create<TestApi>(_TestApiProvider())
 */
internal class CreateFuncTransformer(
    private val pluginContext: IrPluginContext,
    private val debugLogger: DebugLogger,
    private val file: IrFile,
) : IrElementTransformerVoidWithContext() {
    companion object {
        fun errorTypeArgumentNotInterface(implName: String) = "create<$implName> argument is not supported. Type argument needs to be an interface"

        fun errorImplNotFound(
            implName: String,
            className: String,
        ) = "$implName not found, did you apply the Ksp Ktorfit plugin? Use .create$className() instead"

        fun errorClassNotFound(implName: String) = "class $implName not found, did you apply the Ksp Ktorfit plugin?"

        private const val KTORFIT_PACKAGE = "de.jensklingenberg.ktorfit.Ktorfit"
        private const val KTORFIT_CREATE = "create"
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    override fun visitExpression(expression: IrExpression): IrExpression {
        // Find exampleKtorfit.create<TestApi>()
        (expression as? IrCall)?.let { irCall ->
            if (irCall.typeArguments.isNotEmpty()) {
                if (!expression.symbol.owner.symbol
                        .toString()
                        .contains(KTORFIT_PACKAGE)
                ) {
                    return expression
                }
                if (expression.symbol.owner.name
                        .asString() != KTORFIT_CREATE
                ) {
                    return expression
                }

                if (expression.hasRegularParameters()) {
                    return expression
                }

                // Get T from create<T>()
                val argumentType = irCall.typeArguments.firstOrNull() ?: return expression
                val classFqName = argumentType.classFqName

                if (!argumentType.isInterface()) {
                    throw IllegalStateException(
                        errorTypeArgumentNotInterface(argumentType.dumpKotlinLike()),
                    )
                }

                if (classFqName == null) {
                    throw IllegalStateException(
                        errorClassNotFound(argumentType.toIrBasedKotlinType().getKotlinTypeFqName(false))
                    )
                }

                val packageName = classFqName.packageName
                val className = classFqName.shortName().toString()
                val providerClassName = "_$className" + "Provider"

                // Find the class _TestApiProvider
                val classId = ClassId(FqName(packageName), Name.identifier(providerClassName))
                val implClassSymbol =
                    pluginContext.findClass(classId, file)
                        ?: throw IllegalStateException(errorImplNotFound(providerClassName, className))

                val newConstructor = implClassSymbol.constructors.first()

                // Create the constructor call for _ExampleApiProvider()
                val newCall =
                    IrConstructorCallImpl(
                        0,
                        0,
                        type = implClassSymbol.owner.defaultType,
                        symbol = newConstructor,
                        0,
                        0,
                    )

                // Set _ExampleApiProvider() as argument for create<ExampleApi>()
                irCall.arguments[1] = newCall

                debugLogger.log(
                    "Transformed " + argumentType.toIrBasedKotlinType().getKotlinTypeFqName(false).substringAfterLast(".") +
                        " to _$className" +
                        "Provider",
                )
                return super.visitExpression(irCall)
            }
        }
        return super.visitExpression(expression)
    }

    @OptIn(UnsafeDuringIrConstructionAPI::class)
    private fun IrCall.hasRegularParameters(): Boolean {
        val regularParameterIndices =
            symbol
                .owner
                .parameters
                .mapIndexedNotNull { index, param ->
                    if (param.kind == IrParameterKind.Regular) index else null
                }
        return arguments
            .filterIndexed { index, _ ->
                index in regularParameterIndices
            }.firstOrNull() != null
    }
}

/**
 * Finds a class using the new [IrPluginContext.finderForSource] API (Kotlin 2.3.20+),
 * falling back to the deprecated [IrPluginContext.referenceClass] for older versions.
 *
 * The fallback is needed because this plugin runs inside the user's Kotlin compiler,
 * which may be older than 2.3.20. Remove once the minimum supported Kotlin version
 * is >= 2.3.20.
 */
@Suppress("DEPRECATION")
private fun IrPluginContext.findClass(
    classId: ClassId,
    file: IrFile,
): IrClassSymbol? =
    try {
        finderForSource(file).findClass(classId)
    } catch (_: NoSuchMethodError) {
        referenceClass(classId)
    }

private val FqName?.packageName: String
    get() {
        return this.toString().substringBeforeLast(".")
    }
