package de.jensklingenberg.ktorfit

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*

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

    override fun visitBlock(expression: IrBlock): IrExpression {
        expression.transform(CreateFuncTransformer(pluginContext, debugLogger), null)
        return super.visitBlock(expression)
    }

    override fun visitFunctionNew(declaration: IrFunction): IrStatement {
        declaration.transform(CreateFuncTransformer(pluginContext, debugLogger), null)

        return super.visitFunctionNew(declaration)
    }

    override fun visitExpression(expression: IrExpression): IrExpression {
        expression.transform(CreateFuncTransformer(pluginContext, debugLogger), null)

        return super.visitExpression(expression)
    }

    override fun visitFunctionReference(expression: IrFunctionReference): IrExpression {
        expression.transform(CreateFuncTransformer(pluginContext, debugLogger), null)

        return super.visitFunctionReference(expression)
    }

    override fun visitBlockBody(body: IrBlockBody): IrBody {
        body.transform(CreateFuncTransformer(pluginContext, debugLogger), null)
        return super.visitBlockBody(body)
    }

    override fun visitFunctionExpression(expression: IrFunctionExpression): IrExpression {
        expression.transform(CreateFuncTransformer(pluginContext, debugLogger), null)
        return super.visitFunctionExpression(expression)
    }
}