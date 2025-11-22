package de.jensklingenberg.ktorfit.model

import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toAnnotationSpec
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.HEAD
import de.jensklingenberg.ktorfit.http.HTTP
import de.jensklingenberg.ktorfit.http.OPTIONS
import de.jensklingenberg.ktorfit.http.PATCH
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.model.annotations.FormUrlEncoded
import de.jensklingenberg.ktorfit.model.annotations.FunctionAnnotation
import de.jensklingenberg.ktorfit.model.annotations.Headers
import de.jensklingenberg.ktorfit.model.annotations.HttpMethod
import de.jensklingenberg.ktorfit.model.annotations.HttpMethodAnnotation
import de.jensklingenberg.ktorfit.model.annotations.Multipart
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.Body
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.Field
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.FieldMap
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.Header
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.HeaderMap
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.Path
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.RequestBuilder
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.RequestType
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.Tag
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation.Url
import de.jensklingenberg.ktorfit.poetspec.findTypeName
import de.jensklingenberg.ktorfit.utils.anyInstance
import de.jensklingenberg.ktorfit.utils.getFormUrlEncodedAnnotation
import de.jensklingenberg.ktorfit.utils.getHeaderAnnotation
import de.jensklingenberg.ktorfit.utils.getKsFile
import de.jensklingenberg.ktorfit.utils.getMultipartAnnotation
import de.jensklingenberg.ktorfit.utils.getStreamingAnnotation
import de.jensklingenberg.ktorfit.utils.isSuspend
import de.jensklingenberg.ktorfit.utils.parseHTTPMethodAnno
import de.jensklingenberg.ktorfit.utils.resolveTypeName
import de.jensklingenberg.ktorfit.utils.toClassName
import de.jensklingenberg.ktorfit.http.FormUrlEncoded as KtorfitFormUrlEncoded
import de.jensklingenberg.ktorfit.http.Headers as KtorfitHeaders
import de.jensklingenberg.ktorfit.http.Multipart as KtorfitMultipart
import de.jensklingenberg.ktorfit.http.Streaming as KtorfitStreaming

data class FunctionData(
    val name: String,
    val returnType: ReturnTypeData,
    val isSuspend: Boolean = false,
    val parameterDataList: List<ParameterData>,
    val annotations: List<FunctionAnnotation> = emptyList(),
    val httpMethodAnnotation: HttpMethodAnnotation,
    val modifiers: List<KModifier> = emptyList(),
    val optInAnnotations: List<AnnotationSpec>,
    val nonKtorfitAnnotations: List<AnnotationSpec>,
    val matchingConverterFunctions: KSFunctionDeclaration?
)

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
    logger: KSPLogger,
    addImport: (String) -> Unit,
    ktorfitLib: Boolean
): FunctionData {
    val funcDeclaration = this
    val functionName = funcDeclaration.simpleName.asString()
    val functionParameters = funcDeclaration.parameters.map { it.createParameterData(logger) }

    val resolvedReturnType =
        funcDeclaration.returnType?.resolve() ?: throw IllegalStateException("Return type not found")

    val returnType =
        ReturnTypeData(
            name = resolvedReturnType.resolveTypeName(),
            parameterType = resolvedReturnType,
            typeName = findTypeName(resolvedReturnType, funcDeclaration.getKsFile().filePath),
        )

    val functionAnnotationList = mutableListOf<FunctionAnnotation>()

    funcDeclaration.getMultipartAnnotation()?.let {
        functionAnnotationList.add(it)
    }

    if (funcDeclaration.typeParameters.isNotEmpty()) {
        logger.error(
            KtorfitError.FUNCTION_OR_PARAMETERS_TYPES_MUST_NOT_INCLUDE_ATYPE_VARIABLE_OR_WILDCARD,
            funcDeclaration,
        )
    }

    funcDeclaration.getHeaderAnnotation()?.let { headers ->
        headers.value.forEach {
            // Check if headers are in valid format
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
                funcDeclaration,
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
                funcDeclaration,
            )
        }
        functionAnnotationList.add(streaming)
    }

    val httpMethodAnnoList = getHttpMethodAnnotations(funcDeclaration)

    if (httpMethodAnnoList.isEmpty()) {
        logger.error(KtorfitError.noHttpAnnotationAt(functionName), funcDeclaration)
    } else {
        addImport("io.ktor.http.HttpMethod")
    }

    if (httpMethodAnnoList.size > 1) {
        logger.error(
            KtorfitError.ONLY_ONE_HTTP_METHOD_IS_ALLOWED + "Found: " +
                    httpMethodAnnoList.joinToString {
                        it.httpMethod.keyword
                    } + " at " + functionName,
            funcDeclaration,
        )
    }

    val firstHttpMethodAnnotation = httpMethodAnnoList.first()

    val isEmptyHttpPathWithoutUrlAnnotation =
        firstHttpMethodAnnotation.path.isEmpty() && functionParameters.none { it.hasAnnotation<Url>() }
    if (isEmptyHttpPathWithoutUrlAnnotation) {
        logger.error(
            KtorfitError.missingEitherKeywordUrlOrUrlParameter(firstHttpMethodAnnotation.httpMethod.keyword),
            funcDeclaration,
        )
    }

    if (functionParameters.filter { it.hasAnnotation<RequestBuilder>() }.size > 1) {
        logger.error(
            KtorfitError.ONLY_ONE_REQUEST_BUILDER_IS_ALLOWED + " Found: " + httpMethodAnnoList.joinToString { it.toString() } + " at " +
                    functionName,
            funcDeclaration,
        )
    }

    when (firstHttpMethodAnnotation.httpMethod) {
        HttpMethod.POST, HttpMethod.PUT, HttpMethod.PATCH -> {}
        else -> {
            if (functionAnnotationList.anyInstance<Multipart>()) {
                logger.error(
                    KtorfitError.MULTIPART_CAN_ONLY_BE_SPECIFIED_ON_HTTPMETHODS,
                    funcDeclaration,
                )
            }

            if (funcDeclaration.getFormUrlEncodedAnnotation() != null) {
                logger.error(
                    KtorfitError.FORM_URL_ENCODED_CAN_ONLY_BE_SPECIFIED_ON_HTTP_METHODS_WITH_REQUEST_BODY,
                    funcDeclaration,
                )
            }
        }
    }

    functionParameters.forEach { parameterData ->
        parameterData.annotations.forEach {
            if (it is Path) {
                if (!firstHttpMethodAnnotation.path.contains("{${it.value}}")) {
                    logger.error(
                        KtorfitError.missingXInRelativeUrlPath(it.value),
                        funcDeclaration,
                    )
                }
            }

            if (it is Header || it is HeaderMap) {
                addImport("io.ktor.client.request.headers")
            }

            if (it is Tag) {
                addImport("io.ktor.util.AttributeKey")
            }

            if (it is Body ||
                it is ParameterAnnotation.PartMap ||
                it is ParameterAnnotation.Part ||
                it is FieldMap ||
                it is Field
            ) {
                addImport("io.ktor.client.request.setBody")
            }

            if (it is Path && !it.encoded) {
                addImport("io.ktor.http.encodeURLPath")
            }

            if (it is RequestType) {
                addImport("kotlin.reflect.cast")
            }

            if (it is Path && firstHttpMethodAnnotation.path.isEmpty()) {
                logger.error(
                    KtorfitError.PATH_CAN_ONLY_BE_USED_WITH_RELATIVE_URL_ON + "@${firstHttpMethodAnnotation.httpMethod.keyword}",
                    funcDeclaration,
                )
            }

            if (it is Url) {
                if (functionParameters.filter { it.hasAnnotation<Url>() }.size > 1) {
                    logger.error(KtorfitError.MULTIPLE_URL_METHOD_ANNOTATIONS_FOUND, funcDeclaration)
                }
                if (firstHttpMethodAnnotation.path.isNotEmpty()) {
                    logger.error(
                        KtorfitError.urlCanOnlyBeUsedWithEmpty(firstHttpMethodAnnotation.httpMethod.keyword),
                        funcDeclaration,
                    )
                }
            }

            if (it is Field && funcDeclaration.getFormUrlEncodedAnnotation() == null) {
                logger.error(KtorfitError.FIELD_PARAMETERS_CAN_ONLY_BE_USED_WITH_FORM_ENCODING, funcDeclaration)
            }

            if (it is FieldMap && funcDeclaration.getFormUrlEncodedAnnotation() == null) {
                logger.error(
                    KtorfitError.FIELD_MAP_PARAMETERS_CAN_ONLY_BE_USED_WITH_FORM_ENCODING,
                    funcDeclaration,
                )
            }

            if (it is Body && funcDeclaration.getFormUrlEncodedAnnotation() != null) {

            }
        }
    }

    functionAnnotationList.forEach {
        if (it is Headers || it is FormUrlEncoded) {
            addImport("io.ktor.client.request.headers")
        }

        if (it is FormUrlEncoded ||
            it is Multipart ||
            functionParameters.any { param -> param.hasAnnotation<Field>() || param.hasAnnotation<ParameterAnnotation.Part>() }
        ) {
            addImport("io.ktor.client.request.forms.FormDataContent")
            addImport("io.ktor.client.request.forms.MultiPartFormDataContent")
            addImport("io.ktor.client.request.forms.formData")
            addImport("io.ktor.http.Parameters")
        }
    }

    val modifiers =
        mutableListOf(KModifier.OVERRIDE).also {
            if (this.isSuspend) {
                it.add(KModifier.SUSPEND)
            }
        }

    val annotations =
        funcDeclaration.annotations
            .map { it.toAnnotationSpec() }

    val optInAnnotations: MutableList<AnnotationSpec> = mutableListOf()
    val nonKtorfitAnnotations: MutableList<AnnotationSpec> = mutableListOf()

    annotations.forEach { annotation ->
        val className = annotation.toClassName()
        if (className.simpleName == "OptIn") {
            optInAnnotations.add(annotation)
            return@forEach
        }
        if (functionalKtorfitAnnotation.contains(className)) return@forEach
        nonKtorfitAnnotations.add(annotation)
        addImport(className.canonicalName)
    }

    val convertersClasses = this.parentDeclaration?.annotations?.toList().orEmpty().getConverters()
    val conv = if (isSuspend) {

        val matchingConverterFunctions = convertersClasses
            .map { it.getDeclaredFunctions().toList() }
            .flatten()
            .firstOrNull {
                it.parameters.firstOrNull()?.type.toString().contains("HttpResponse") &&
                        it.returnType!!.resolve().isAssignableFrom(returnType.parameterType) &&
                        it.isSuspend
            }

        matchingConverterFunctions

    } else {
        val matchingConverterFunctions = convertersClasses
            .map { it.getDeclaredFunctions().toList() }
            .flatten()
            .firstOrNull {
                it.parameters.firstOrNull()?.type.toString().contains("SuspendFunction") &&
                        it.returnType!!.resolve().isAssignableFrom(returnType.parameterType) &&
                        !it.isSuspend
            }

        if(!ktorfitLib && convertersClasses.isNotEmpty() && matchingConverterFunctions==null){
            logger.error("Found converter function for non-suspend function ${functionName} in ${this.parentDeclaration?.simpleName?.asString()}, but the function is not suspend. Make sure this is intended. fun convert( getResponse: suspend () -> HttpResponse): ${returnType.name}")
        }

        if(!ktorfitLib && convertersClasses.isEmpty()){
            logger.error("No converter function for non-suspend function ${functionName} in ${this.parentDeclaration?.simpleName?.asString()}, but the function is not suspend. Make sure this is intended. \n fun convert( getResponse: suspend () -> HttpResponse): ${returnType.name}")
        }

        matchingConverterFunctions
    }
    return FunctionData(
        functionName,
        returnType,
        funcDeclaration.isSuspend,
        functionParameters,
        functionAnnotationList,
        firstHttpMethodAnnotation,
        modifiers,
        optInAnnotations,
        nonKtorfitAnnotations,
        conv
    )
}

private val functionalKtorfitAnnotation =
    listOf(
        GET::class, POST::class, PUT::class, DELETE::class, HEAD::class, OPTIONS::class, PATCH::class, HTTP::class,
        KtorfitHeaders::class, KtorfitFormUrlEncoded::class, KtorfitMultipart::class, KtorfitStreaming::class,
    )
        .map { it.asClassName() }
