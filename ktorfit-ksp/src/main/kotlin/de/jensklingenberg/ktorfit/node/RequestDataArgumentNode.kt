package de.jensklingenberg.ktorfit.node

import de.jensklingenberg.ktorfit.model.MyFunction
import de.jensklingenberg.ktorfit.model.annotations.HttpMethodAnnotation


class RequestDataArgumentNode(
    private val myFunction: MyFunction,
    private val headerArgumentCmd: HeadersArgumentNode,
    private val bodyDataArgumentCmd: BodyDataArgumentNode,
    private val partsArgumentNode: PartsArgumentNode,
    private val urlArgumentNode: RelativeUrlArgumentNode,
    private val queryArgumentNode: QueryArgumentNode,
    private val fieldArgumentsNode: FieldArgumentsNode,
    private val requestBuilderArgumentNode: RequestBuilderArgumentNode,
    private val httpMethodAnnotation: HttpMethodAnnotation
) : MyNode() {

    override fun toString(): String {
        val methodAnnotation = httpMethodAnnotation
        //METHOD
        val method = "method=\"${methodAnnotation.httpMethod.keyword}\""
        //HEADERS
        val headersText = headerArgumentCmd.toString()
        //BODY
        val body = bodyDataArgumentCmd.toString()
        //URL
        val urlPath = urlArgumentNode.toString()
        val queryText = queryArgumentNode.toString()
        val fieldsText = fieldArgumentsNode.toString()
        val partsText = partsArgumentNode.toString()
        val builderText = requestBuilderArgumentNode.toString()

        val qualifiedTypeName = "qualifiedRawTypeName=\"${myFunction.returnType.qualifiedName}\""

        val args = listOf(
            method,
            urlPath,
            headersText,
            body,
            queryText,
            fieldsText,
            partsText,
            builderText,
            qualifiedTypeName
        ).filter { it.isNotEmpty() }.joinToString(",\n") { it }

        return "val requestData = RequestData($args) \n"
    }
}