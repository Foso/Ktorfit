package de.jensklingenberg.ktorfit.node

import de.jensklingenberg.ktorfit.findAnnotationOrNull
import de.jensklingenberg.ktorfit.model.MyFunction
import de.jensklingenberg.ktorfit.model.annotations.Streaming

/**
 * Create source code for the function body of [FuncNode]
 */
class FuncBodyNode(private val myFunction: MyFunction, private val argumentCmd: RequestDataArgumentNode) : MyNode() {

    override fun toString(): String {
        val returnTypeName = myFunction.returnType.name
        val hasStreaming = myFunction.findAnnotationOrNull<Streaming>() != null

        val typeWithoutOuterType = returnTypeName.substringAfter("<").substringBeforeLast(">")

        val requestDataText = argumentCmd.toString()

        val bodyText = requestDataText + if (myFunction.isSuspend) {
            if (hasStreaming) {
                "return client.prepareRequest(requestData)"
            } else {
                "return client.suspendRequest<${returnTypeName}, $typeWithoutOuterType>(requestData)"
            }
        } else {
            "return client.request<${returnTypeName}, $typeWithoutOuterType>(requestData)"
        }

        return bodyText
    }
}