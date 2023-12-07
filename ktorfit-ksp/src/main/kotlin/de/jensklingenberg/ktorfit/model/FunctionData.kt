package de.jensklingenberg.ktorfit.model

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ksp.toTypeName
import de.jensklingenberg.ktorfit.model.annotations.*
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.*
import de.jensklingenberg.ktorfit.reqBuilderExtension.getReqBuilderExtensionText
import de.jensklingenberg.ktorfit.typeData.addRequestConverterText
import de.jensklingenberg.ktorfit.utils.*

data class FunctionData(
    val name: String,
    val returnType: ReturnTypeData,
    val isSuspend: Boolean = false,
    val parameterDataList: List<ParameterData>,
    val annotations: List<FunctionAnnotation> = emptyList(),
    val httpMethodAnnotation: HttpMethodAnnotation
) {

    fun toFunSpec(resolver: Resolver): FunSpec {
        val returnTypeName = this.returnType.parameterType?.toTypeName()
        val innerReturnType =
            (returnTypeName as? ParameterizedTypeName)?.typeArguments?.first()?.toString() ?: returnTypeName

        return FunSpec.builder(this.name)
            .addModifiers(mutableListOf(KModifier.OVERRIDE).also {
                if (this.isSuspend) {
                    it.add(KModifier.SUSPEND)
                }
            })
            .returns(returnType.parameterType!!.toTypeName())
            .addParameters(this.parameterDataList.map {
                ParameterSpec(it.name, it.type.parameterType!!.toTypeName())
            })
            .addRequestConverterText(this.parameterDataList)
            .addStatement(
                getReqBuilderExtensionText(
                    this,
                    resolver
                )
            )
            .addStatement(
                "val ${typeDataClass.objectName} = ${typeDataClass.name}.createTypeData("
            )
            .addStatement(
                "qualifiedTypename = \"%L\",",
                this.returnType.parameterType.toTypeName().toString().removeWhiteSpaces()
            )
            .addStatement("typeInfo = typeInfo<%L>())\n", this.returnType.parameterType.toTypeName())
            .addStatement(
                "return %L.%L<${returnTypeName}>(%L,${extDataClass.objectName})%L",
                converterHelper.objectName,
                if (this.isSuspend) {
                    "suspendRequest"
                } else {
                    "request"
                },
                typeDataClass.objectName,
                if (this.returnType.parameterType.isMarkedNullable) {
                    ""
                } else {
                    "!!"
                }
            )
            .build()
    }

}

/**
 * Collect all [HttpMethodAnnotation] from a [KSFunctionDeclaration]
 * @return list of [HttpMethodAnnotation]
 */
private fun getHttpMethodAnnotations(ksFunctionDeclaration: KSFunctionDeclaration): List<HttpMethodAnnotation> {
    val getAnno = ksFunctionDeclaration.parseHTTPMethodAnno("GET")
    val putAnno = ksFunctionDeclaration.parseHTTPMethodAnno("PUT")
    val postAnno = ksFunctionDeclaration.parseHTTPMethodAnno("POST")
    val deleteAnno = ksFunctionDeclaration.parseHTTPMethodAnno("DELETE")
    val headAnno = ksFunctionDeclaration.parseHTTPMethodAnno("HEAD")
    val optionsAnno = ksFunctionDeclaration.parseHTTPMethodAnno("OPTIONS")
    val patchAnno = ksFunctionDeclaration.parseHTTPMethodAnno("PATCH")
    val httpAnno = ksFunctionDeclaration.parseHTTPMethodAnno("HTTP")

    return listOfNotNull(getAnno, postAnno, putAnno, deleteAnno, headAnno, optionsAnno, patchAnno, httpAnno)
}

fun KSFunctionDeclaration.toFunctionData(
    logger: KSPLogger
): FunctionData {
    val funcDeclaration = this
    val functionName = funcDeclaration.simpleName.asString()
    val functionParameters = funcDeclaration.parameters.map { it.createParameterData(logger) }

    val resolvedReturnType = funcDeclaration.returnType?.resolve()

    val returnType = ReturnTypeData(
        name = resolvedReturnType.resolveTypeName(),
        parameterType = funcDeclaration.returnType?.resolve()
    )

    val functionAnnotationList = mutableListOf<FunctionAnnotation>()

    funcDeclaration.getMultipartAnnotation()?.let {
        functionAnnotationList.add(it)
    }

    if (funcDeclaration.typeParameters.isNotEmpty()) {
        logger.error(
            KtorfitError.FUNCTION_OR_PARAMETERS_TYPES_MUST_NOT_INCLUDE_ATYPE_VARIABLE_OR_WILDCARD,
            funcDeclaration
        )
    }

    funcDeclaration.getHeaderAnnotation()?.let { headers ->
        headers.value.forEach {
            //Check if headers are in valid format
            try {
                val (key, value) = it.split(":")
            } catch (exception: Exception) {
                logger.error(KtorfitError.HEADERS_VALUE_MUST_BE_IN_FORM + it, funcDeclaration)
            }
        }
        functionAnnotationList.add(headers)
    }

    funcDeclaration.getFormUrlEncodedAnnotation()?.let { formUrlEncoded ->
        val isWithoutFieldOrFieldMap =
            functionParameters.none { it.hasAnnotation<Field>() || it.hasAnnotation<FieldMap>() }
        if (isWithoutFieldOrFieldMap) {
            logger.error(
                KtorfitError.FORM_ENCODED_METHOD_MUST_CONTAIN_AT_LEAST_ONE_FIELD_OR_FIELD_MAP,
                funcDeclaration
            )
        }

        if (funcDeclaration.getMultipartAnnotation() != null) {
            logger.error(KtorfitError.ONLY_ONE_ENCODING_ANNOTATION_IS_ALLOWED, funcDeclaration)
        }

        functionAnnotationList.add(formUrlEncoded)
    }

    funcDeclaration.getStreamingAnnotation()?.let { streaming ->
        val returnsHttpStatement = returnType.name == "HttpStatement"
        if (!returnsHttpStatement) {
            logger.error(
                KtorfitError.FOR_STREAMING_THE_RETURN_TYPE_MUST_BE_HTTP_STATEMENT,
                funcDeclaration
            )
        }
        functionAnnotationList.add(streaming)
    }

    val httpMethodAnnoList = getHttpMethodAnnotations(funcDeclaration)

    if (httpMethodAnnoList.isEmpty()) {
        logger.error(KtorfitError.NO_HTTP_ANNOTATION_AT(functionName), funcDeclaration)
    }

    if (httpMethodAnnoList.size > 1) {
        logger.error(KtorfitError.ONLY_ONE_HTTP_METHOD_IS_ALLOWED + "Found: " + httpMethodAnnoList.joinToString { it.httpMethod.keyword } + " at " + functionName,
            funcDeclaration)
    }

    val firstHttpMethodAnnotation = httpMethodAnnoList.first()

    val isEmptyHttpPathWithoutUrlAnnotation =
        firstHttpMethodAnnotation.path.isEmpty() && functionParameters.none { it.hasAnnotation<Url>() }
    if (isEmptyHttpPathWithoutUrlAnnotation) {
        logger.error(
            KtorfitError.MISSING_EITHER_KEYWORD_URL_OrURL_PARAMETER(firstHttpMethodAnnotation.httpMethod.keyword),
            funcDeclaration
        )
    }

    if (functionParameters.filter { it.hasAnnotation<RequestBuilder>() }.size > 1) {
        logger.error(KtorfitError.ONLY_ONE_REQUEST_BUILDER_IS_ALLOWED + " Found: " + httpMethodAnnoList.joinToString { it.toString() } + " at " + functionName,
            funcDeclaration)
    }

    when (firstHttpMethodAnnotation.httpMethod) {
        HttpMethod.POST, HttpMethod.PUT, HttpMethod.PATCH -> {}
        else -> {
            if (firstHttpMethodAnnotation is CustomHttp && firstHttpMethodAnnotation.hasBody) {
                //Do nothing
            } else if (functionParameters.any { it.hasAnnotation<Body>() }) {
                logger.error(KtorfitError.NON_BODY_HTTP_METHOD_CANNOT_CONTAIN_BODY, funcDeclaration)
            }

            if (functionAnnotationList.anyInstance<Multipart>()) {
                logger.error(
                    KtorfitError.MULTIPART_CAN_ONLY_BE_SPECIFIED_ON_HTTPMETHODS,
                    funcDeclaration
                )
            }

            if (funcDeclaration.getFormUrlEncodedAnnotation() != null) {
                logger.error(
                    KtorfitError.FORM_URL_ENCODED_CAN_ONLY_BE_SPECIFIED_ON_HTTP_METHODS_WITH_REQUEST_BODY,
                    funcDeclaration
                )
            }
        }
    }

    if (functionParameters.any { it.hasAnnotation<Path>() } && firstHttpMethodAnnotation.path.isEmpty()) {
        logger.error(
            KtorfitError.PATH_CAN_ONLY_BE_USED_WITH_RELATIVE_URL_ON + "@${firstHttpMethodAnnotation.httpMethod.keyword}",
            funcDeclaration
        )
    }

    functionParameters.filter { it.hasAnnotation<Path>() }.forEach {
        val pathAnnotation = it.findAnnotationOrNull<Path>()
        if (!firstHttpMethodAnnotation.path.contains("{${pathAnnotation?.value ?: ""}}")) {
            logger.error(
                KtorfitError.MISSING_X_IN_RELATIVE_URL_PATH(pathAnnotation?.value.orEmpty()),
                funcDeclaration
            )
        }
    }

    if (functionParameters.any { it.hasAnnotation<Url>() }) {
        if (functionParameters.filter { it.hasAnnotation<Url>() }.size > 1) {
            logger.error(KtorfitError.MULTIPLE_URL_METHOD_ANNOTATIONS_FOUND, funcDeclaration)
        }
        if (firstHttpMethodAnnotation.path.isNotEmpty()) {
            logger.error(
                KtorfitError.URL_CAN_ONLY_BE_USED_WITH_EMPY(firstHttpMethodAnnotation.httpMethod.keyword),
                funcDeclaration
            )
        }
    }

    if (functionParameters.any { it.hasAnnotation<Field>() } && funcDeclaration.getFormUrlEncodedAnnotation() == null) {
        logger.error(KtorfitError.FIELD_PARAMETERS_CAN_ONLY_BE_USED_WITH_FORM_ENCODING, funcDeclaration)
    }

    if (functionParameters.any { it.hasAnnotation<FieldMap>() } && funcDeclaration.getFormUrlEncodedAnnotation() == null) {
        logger.error(
            KtorfitError.FIELD_MAP_PARAMETERS_CAN_ONLY_BE_USED_WITH_FORM_ENCODING,
            funcDeclaration
        )
    }

    if (functionParameters.any { it.hasAnnotation<Body>() } && funcDeclaration.getFormUrlEncodedAnnotation() != null) {
        logger.error(
            KtorfitError.BODY_PARAMETERS_CANNOT_BE_USED_WITH_FORM_OR_MULTI_PART_ENCODING,
            funcDeclaration
        )
    }

    return FunctionData(
        functionName,
        returnType,
        funcDeclaration.isSuspend,
        functionParameters,
        functionAnnotationList,
        firstHttpMethodAnnotation
    )
}