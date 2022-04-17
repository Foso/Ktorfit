package de.jensklingenberg.ktorfit.requestData

import de.jensklingenberg.ktorfit.hasAnnotation
import de.jensklingenberg.ktorfit.model.MyFunction
import de.jensklingenberg.ktorfit.model.annotations.Body


class RequestDataArgumentNode(private val myFunction: MyFunction) {

    val params = myFunction.params

    private fun getRequestBuilderText(): String {
        return (params.find { it.hasRequestBuilderAnno }?.let {
            "requestBuilder = " + it.name
        } ?: "")
    }

    private fun getBodyDataText(): String {
        var bodyText = ""
        params.firstOrNull { it.hasAnnotation<Body>() }?.let {
            bodyText = "bodyData = " + it.name
        }
        return bodyText
    }

    override fun toString(): String {
        val methodAnnotation = myFunction.httpMethodAnnotation
        //METHOD
        val method = "method=\"${methodAnnotation.httpMethod.keyword}\""
        //HEADERS
        val headersText = getHeadersArgumentNode(myFunction.annotations, myFunction.params)
        //BODY
        val body = getBodyDataText()
        //URL
        val urlPath = getRelativeUrlArgumentNode(myFunction)
        val queryText = getQueryArgumentNode(myFunction.params)
        val fieldsText = getFieldArgumentsText(myFunction.params)
        val partsText = getPartsArgumentNode(myFunction.params)
        val builderText = getRequestBuilderText()

        val qualifiedTypeName = "qualifiedRawTypeName=\"${myFunction.returnType.qualifiedName}\""

        val args = listOf<String>(
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