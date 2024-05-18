package de.jensklingenberg.ktorfit

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression

class ElementTransformer(
    private val pluginContext: IrPluginContext,
    private val debugLogger: DebugLogger
) : IrElementTransformerVoidWithContext() {

    override fun visitValueParameterNew(declaration: IrValueParameter): IrStatement {
        declaration.transform(CreateFuncTransformer(pluginContext,debugLogger), null)
        return super.visitValueParameterNew(declaration)
    }

    override fun visitPropertyNew(declaration: IrProperty): IrStatement {
        declaration.transform(CreateFuncTransformer(pluginContext, debugLogger), null)
        return super.visitPropertyNew(declaration)
    }

    override fun visitCall(expression: IrCall): IrExpression {
        expression.transform(CreateFuncTransformer(pluginContext, debugLogger), null)
        return super.visitCall(expression)
    }

    override fun visitVariable(declaration: IrVariable): IrStatement {
        declaration.transform(CreateFuncTransformer(pluginContext, debugLogger), null)
        return super.visitVariable(declaration)
    }

    override fun visitConstructorCall(expression: IrConstructorCall): IrExpression {
        expression.transform(CreateFuncTransformer(pluginContext, debugLogger), null)
        return super.visitConstructorCall(expression)
    }


    override fun visitFunctionExpression(expression: IrFunctionExpression): IrExpression {
        expression.transform(CreateFuncTransformer(pluginContext, debugLogger), null)
        return super.visitFunctionExpression(expression)
    }
}