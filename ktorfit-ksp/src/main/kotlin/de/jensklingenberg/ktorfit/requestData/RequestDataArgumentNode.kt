package de.jensklingenberg.ktorfit.requestData

import de.jensklingenberg.ktorfit.hasAnnotation
import de.jensklingenberg.ktorfit.model.FunctionData
import de.jensklingenberg.ktorfit.model.annotations.Body


class RequestDataArgumentNode(private val myFunction: FunctionData) {

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
        val headersText = getHeadersArgumentText(myFunction.annotations, myFunction.params)
        //BODY
        val body = getBodyDataText()
        //URL
        val urlPath = getRelativeUrlArgumentText(myFunction.httpMethodAnnotation,myFunction.params)
        val queryText = getQueryArgumentText(myFunction.params)
        val fieldsText = getFieldArgumentsText(myFunction.params)
        val partsText = getPartsArgumentText(myFunction.params)
        val builderText = getRequestBuilderText()

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