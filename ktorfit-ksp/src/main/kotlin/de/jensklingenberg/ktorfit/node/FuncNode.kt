package de.jensklingenberg.ktorfit.node

import de.jensklingenberg.ktorfit.model.MyFunction

/**
 * Generate the code for the overridden functions in the implemenation class
 */
class FuncNode(
    private val myFunction: MyFunction,
    private val funcBodyNode: FuncBodyNode,
    private val paramCmd: List<ParamNode>
) :
    MyNode() {

    override fun toString(): String {
        val returnTypeName = myFunction.returnType.name
        val paramsText = paramCmd.joinToString { it.toString() }
        val suspendModifierText = "suspend".takeIf { myFunction.isSuspend } ?: ""
        val functionName = myFunction.name

        return """override $suspendModifierText fun $functionName($paramsText): $returnTypeName {
$funcBodyNode
                    }
                    """
    }
}