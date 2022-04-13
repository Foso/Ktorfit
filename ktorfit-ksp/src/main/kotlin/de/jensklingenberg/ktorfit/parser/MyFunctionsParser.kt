package de.jensklingenberg.ktorfit.parser

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import de.jensklingenberg.ktorfit.*
import de.jensklingenberg.ktorfit.model.MyFunction
import de.jensklingenberg.ktorfit.model.MyType
import de.jensklingenberg.ktorfit.model.annotations.*


fun getHttpMethodAnnotations(func: KSFunctionDeclaration): List<HttpMethodAnnotation> {
    val getAnno = func.parseHTTPMethodAnno("GET")
    val putAnno = func.parseHTTPMethodAnno("PUT")
    val postAnno = func.parseHTTPMethodAnno("POST")
    val deleteAnno = func.parseHTTPMethodAnno("DELETE")
    val headAnno = func.parseHTTPMethodAnno("HEAD")
    val optionsAnno = func.parseHTTPMethodAnno("OPTIONS")
    val patchAnno = func.parseHTTPMethodAnno("PATCH")
    val httpAnno = func.parseHTTPMethodAnno("HTTP")

    return listOfNotNull(getAnno, postAnno, putAnno, deleteAnno, headAnno, optionsAnno, patchAnno, httpAnno)
}

fun getMyFunctionsList(
    ksFunctionDeclarationList: List<KSFunctionDeclaration>,
    logger: KSPLogger
): List<MyFunction> {

    return ksFunctionDeclarationList.map { funcDeclaration ->

        val functionName = funcDeclaration.simpleName.asString()
        val functionParameters = funcDeclaration.parameters.map { getMyParamList(it, logger) }

        val returnType = MyType(
            funcDeclaration.returnType?.resolve().resolveTypeName(),
            funcDeclaration.returnType?.resolve()?.declaration?.qualifiedName?.asString() ?: ""
        )

        val functionAnnotationList = mutableListOf<FunctionAnnotation>()

        with(funcDeclaration) {
            if (funcDeclaration.typeParameters.isNotEmpty()) {
                logger.ktorfitError(
                    "function or parameters types must not include a type variable or wildcard:",
                    funcDeclaration
                )
            }


            this.getHeadersAnnotation()?.let {
                functionAnnotationList.add(it)
            }

            this.getFormUrlEncodedAnnotation()?.let { formUrlEncoded ->
                if (functionParameters.none { it.hasAnnotation<Field>() } && functionParameters.none { it.hasAnnotation<FieldMap>() }) {
                    logger.ktorfitError(
                        "Form-encoded method must contain at least one @Field or @FieldMap.",
                        funcDeclaration
                    )
                }

                functionAnnotationList.add(formUrlEncoded)
            }

            this.getStreamingAnnotation()?.let { streaming ->
                if (returnType.name != "HttpStatement") {
                    logger.ktorfitError(
                        "For streaming the return type must be io.ktor.client.statement.HttpStatement",
                        funcDeclaration
                    )
                }
                functionAnnotationList.add(streaming)
            }

            this.getMultipartAnnotation()?.let {
                functionAnnotationList.add(it)
            }
        }

        val httpMethodAnnoList = getHttpMethodAnnotations(funcDeclaration)

        if (httpMethodAnnoList.isEmpty()) {
            logger.ktorfitError("No Http annotation $functionName", funcDeclaration)
        }

        if (httpMethodAnnoList.size > 1) {
            logger.ktorfitError("Only one HTTP method is allowed. Found: " + httpMethodAnnoList.joinToString { it.httpMethod.keyword } + " at " + functionName,
                funcDeclaration)
        }

        val httpMethodAnno = httpMethodAnnoList.first()

        if (httpMethodAnno.path.isEmpty() && functionParameters.none { it.hasAnnotation<Url>() }) {
            logger.ktorfitError(
                "Missing either @${httpMethodAnno.httpMethod.keyword} URL or @Url parameter.",
                funcDeclaration
            )
        }

        if (functionParameters.filter { it.hasRequestBuilderAnno }.size > 1) {
            logger.ktorfitError("Only one RequestBuilder is allowed. Found: " + httpMethodAnnoList.joinToString { it.toString() } + " at " + functionName,
                funcDeclaration)
        }

        when (httpMethodAnno.httpMethod) {
            HttpMethod.POST, HttpMethod.PUT, HttpMethod.PATCH -> {}
            else -> {
                if (functionParameters.any { it.hasAnnotation<Body>() }) {
                    logger.ktorfitError("Non-body HTTP method cannot contain @Body.", funcDeclaration)
                }

                if (functionAnnotationList.any { it is Multipart }) {
                    logger.ktorfitError(
                        "Multipart can only be specified on HTTP methods with request body (e.g., @POST)",
                        funcDeclaration
                    )
                }

                if (funcDeclaration.getFormUrlEncodedAnnotation() != null) {
                    logger.ktorfitError(
                        "FormUrlEncoded can only be specified on HTTP methods with request body (e.g., @POST).",
                        funcDeclaration
                    )
                }
            }
        }

        if (functionParameters.any { it.hasAnnotation<Path>() } && httpMethodAnno.path.isEmpty()) {
            logger.ktorfitError(
                "@Path can only be used with relative url on @GET",
                funcDeclaration
            )
        }

        if (funcDeclaration.getFormUrlEncodedAnnotation() != null && funcDeclaration.getMultipartAnnotation() != null) {
            logger.ktorfitError("Only one encoding annotation is allowed.", funcDeclaration)
        }

        if (functionParameters.any { it.hasAnnotation<Url>() }) {
            if (functionParameters.filter { it.hasAnnotation<Url>() }.size > 1) {
                logger.ktorfitError("Multiple @Url method annotations found", funcDeclaration)
            }
            if (httpMethodAnno.path.isNotEmpty()) {
                logger.ktorfitError(
                    "@Url cannot be used with @${httpMethodAnno.httpMethod.keyword} URL",
                    funcDeclaration
                )
            }
        }

        if (functionParameters.any { it.hasAnnotation<Field>() }) {
            if (funcDeclaration.getFormUrlEncodedAnnotation() == null) {
                logger.ktorfitError("@Field parameters can only be used with form encoding", funcDeclaration)
            }
        }

        if (functionParameters.any { it.hasAnnotation<Body>() }) {
            if (funcDeclaration.getFormUrlEncodedAnnotation() != null) {
                logger.ktorfitError("@Body parameters cannot be used with form or multi-part encoding", funcDeclaration)
            }
        }

        return@map MyFunction(
            functionName,
            returnType,
            funcDeclaration.isSuspend,
            functionParameters,
            functionAnnotationList,
            httpMethodAnno
        )

    }
}
