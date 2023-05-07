package de.jensklingenberg.ktorfit.internal

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.util.reflect.*
import kotlin.reflect.KClass
import kotlin.reflect.cast


internal class KtorfitClient(private val ktorfit: Ktorfit) : Client {

    private val httpClient: HttpClient = ktorfit.httpClient

    /**
     * Converts [value] to an URL encoded value
     */
    private fun encode(value: Any): String {
        return value.toString().encodeURLParameter()
    }

    /**
     * This will handle all requests for functions without suspend modifier
     */
    override fun <ReturnType, RequestType : Any?> request(
        requestData: RequestData
    ): ReturnType? {

        ktorfit.responseConverters.firstOrNull { converter ->
            converter.supportedType(
                requestData.returnTypeData, false
            )
        }?.let { requestConverter ->
            return requestConverter.wrapResponse<RequestType?>(
                typeData = requestData.returnTypeData,
                requestFunction = {
                    try {
                        val data =
                            suspendRequest<HttpResponse, HttpResponse>(requestData.copy(returnTypeInfo = typeInfo<HttpResponse>()))
                        Pair(requestData.requestTypeInfo, data)
                    } catch (ex: Exception) {
                        throw ex
                    }
                },
                ktorfit = ktorfit
            ) as ReturnType?
        }

        val typeIsNullable = requestData.returnTypeData.isNullable
        return if (typeIsNullable) {
            null
        } else {
            throw IllegalArgumentException("Add a RequestConverter for " + requestData.returnTypeData.qualifiedName + " or make function suspend")
        }

    }

    /**
     * This will handle all requests for functions with suspend modifier
     * Used by generated Code
     */
    override suspend fun <ReturnType, RequestType : Any?> suspendRequest(
        requestData: RequestData
    ): ReturnType? {
        try {

            if (requestData.returnTypeInfo.type == HttpStatement::class) {
                return httpClient.prepareRequest {
                    requestBuilder(requestData)
                } as ReturnType
            }

            if (requestData.returnTypeInfo.type == HttpResponse::class) {
                val response = httpClient.request {
                    requestBuilder(requestData)
                }
                return response as ReturnType
            }

            ktorfit.suspendResponseConverters.firstOrNull { converter ->
                converter.supportedType(
                    requestData.returnTypeData, true
                )
            }?.let {
                return it.wrapSuspendResponse<RequestType>(
                    typeData = requestData.returnTypeData,
                    requestFunction = {
                        Pair(requestData.requestTypeInfo, httpClient.request {
                            requestBuilder(requestData)
                        })
                    }, ktorfit
                ) as ReturnType
            }

            val response = httpClient.request {
                requestBuilder(requestData)
            }
            return response.body(requestData.returnTypeInfo)

        } catch (exception: Exception) {
            val typeIsNullable = requestData.returnTypeData.isNullable
            return if (typeIsNullable) {
                null
            } else {
                throw exception
            }
        }
    }

    override fun <T : Any> convertParameterType(data: Any, parameterType: KClass<*>, requestType: KClass<T>): T {
        val requestConverter = ktorfit.requestConverters.firstOrNull {
            it.supportedType(parameterType, requestType)
        }
            ?: throw IllegalArgumentException("No RequestConverter found to convert ${parameterType.simpleName} to ${requestType.simpleName}")
        return requestType.cast(requestConverter.convert(data))
    }


    private fun HttpRequestBuilder.requestBuilder(
        requestData: RequestData
    ) {

        handleFields(requestData.fields)
        handleParts(requestData.parts)
        handleQueries(requestData.queries)

        val relativeUrl = getRelativeUrl(requestData.paths, requestData.relativeUrl)

        val requestUrl = getRequestUrl(ktorfit.baseUrl, relativeUrl)

        url(requestUrl)
        requestData.ktorfitRequestBuilder(this)
        requestData.requestBuilder(this)
    }

    private fun getRequestUrl(
        baseUrl: String,
        relativeUrl: String
    ): String {
        return if (relativeUrl.startsWith("http")) {
            relativeUrl
        } else {
            baseUrl + relativeUrl
        }
    }


    /**
     * This method replaces all parts of the [relativeUrl] which have curly braces
     * with their corresponding value
     * @return the relative URL with replaced values
     */
    private fun getRelativeUrl(paths: List<DH>, relativeUrl: String): String {
        var newUrl = relativeUrl
        paths.forEach {

            val newPathValue = if (it.encoded) {
                it.data.toString()
            } else {
                encode(it.data.toString())
            }

            newUrl = newUrl.replace("{${it.key}}", newPathValue)
        }

        return newUrl
    }

    private fun HttpRequestBuilder.handleFields(fields: List<DH>) {
        if (fields.isNotEmpty()) {
            val formParameters = Parameters.build {

                fun append(encoded: Boolean, key: String, value: String) {
                    /**
                     * This is a workaround.
                     * Ktor encodes parameters by default and I don't know
                     * how to deactivate this.
                     * When the value is not encoded it will be given to Ktor unchanged.
                     * If it is encoded, it gets decoded, so Ktor can encode it again.
                     */

                    if (encoded) {
                        append(key, value.decodeURLQueryComponent(plusIsSpace = true))
                    } else {
                        append(key, value)
                    }
                }

                fields.forEach { entry ->

                    when (val data = entry.data) {
                        is List<*> -> {
                            data.filterNotNull().forEach {
                                append(entry.encoded, entry.key, it as String)
                            }
                        }

                        is Array<*> -> {
                            data.filterNotNull().forEach {
                                append(entry.encoded, entry.key, it as String)
                            }
                        }

                        is Map<*, *> -> {
                            for ((key, value) in entry.data as Map<*, *>) {
                                value?.let {
                                    append(entry.encoded, key.toString(), value.toString())
                                }
                            }
                        }

                        null -> {
                            //Ignore this
                        }

                        else -> {
                            append(entry.encoded, entry.key, entry.data.toString())
                        }
                    }
                }
            }
            setBody(FormDataContent(formParameters))

        }
    }

    private fun HttpRequestBuilder.handleQueries(queries: List<DH>) {
        queries.forEach { entry ->

            when (val data = entry.data) {
                is List<*> -> {
                    data.filterNotNull().forEach {
                        setParameter(entry.encoded, entry.key, it.toString())
                    }
                }

                is Array<*> -> {
                    data.filterNotNull().forEach {
                        setParameter(entry.encoded, entry.key, it.toString())
                    }
                }

                is Map<*, *> -> {
                    for ((key, value) in entry.data as Map<*, *>) {
                        value?.let {
                            setParameter(entry.encoded, key.toString(), value.toString())
                        }

                    }
                }

                null -> {
                    //Ignore this query
                }

                else -> {
                    setParameter(entry.encoded, entry.key, entry.data.toString())
                }
            }
        }

    }

    private fun HttpRequestBuilder.handleParts(parts: Map<String, Any>) {
        if (parts.isNotEmpty()) {
            val partDatas = mutableListOf<PartData>()

            parts.forEach {
                if (it.value as? List<PartData> != null) {
                    partDatas.addAll(it.value as List<PartData>)
                }
            }

            val formData = formData {
                parts.filter { it.value is String }.forEach {
                    this@formData.append(it.key, it.value.toString())
                }
            }
            val partDataList = formData + partDatas
            setBody(MultiPartFormDataContent(partDataList))
        }
    }

    private fun HttpRequestBuilder.setParameter(
        encoded: Boolean,
        key: String,
        value: String
    ) {
        if (encoded) {
            if (key.isEmpty()) {
                url.encodedParameters.appendAll(value, emptyList())
            } else {
                encodedParameter(key, value)
            }

        } else {
            if (key.isEmpty()) {
                url.parameters.appendAll(value, emptyList())
            } else {
                parameter(key, value)
            }

        }
    }

    private fun HttpRequestBuilder.encodedParameter(key: String, value: Any): Unit =
        value.let { url.encodedParameters.append(key, it.toString()) }

}
