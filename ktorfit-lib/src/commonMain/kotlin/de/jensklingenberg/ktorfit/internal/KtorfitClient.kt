package de.jensklingenberg.ktorfit.internal

import de.jensklingenberg.ktorfit.Ktorfit

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.util.*
import io.ktor.util.reflect.*

/**
 * This class will be used by the generated Code
 * Please don't use the class directly
 */
@InternalKtorfitApi
class KtorfitClient(val ktorfit: Ktorfit) {

    val httpClient = ktorfit.httpClient

    /**
     * Converts [value] to an URL encoded value
     */
    private fun encode(value: Any): String {
        return value.toString().encodeURLParameter()
    }


    /**
     * This will handle all requests for functions without suspend modifier
     */
    inline fun <reified ReturnType, reified RequestType : Any?> request(
        requestData: RequestData
    ): ReturnType? {

        ktorfit.requestConverters.firstOrNull { converter ->
            converter.supportedType(
                requestData.returnTypeData, false
            )
        }?.let { requestConverter ->
            return requestConverter.wrapResponse<RequestType?>(
                typeData = requestData.returnTypeData,
                requestFunction = {
                    try {
                        val data = suspendRequest<HttpResponse, HttpResponse>(requestData)
                        Pair(typeInfo<RequestType?>(), data)
                    } catch (ex: Exception) {
                        throw ex
                    }
                },
                ktorfit = ktorfit
            ) as ReturnType?
        }

        val typeIsNullable = requestData.returnTypeData.qualifiedName.endsWith("?")
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
    suspend inline fun <reified ReturnType, reified PRequest : Any?> suspendRequest(
        requestData: RequestData
    ): ReturnType? {
        try {
            if (ReturnType::class == HttpStatement::class) {
                return httpClient.prepareRequest {
                    requestBuilder(requestData)
                } as ReturnType
            }
            val response = httpClient.request {
                requestBuilder(requestData)
            }
            if (ReturnType::class == HttpResponse::class) {
                return response as ReturnType
            }

            ktorfit.suspendResponseConverters.firstOrNull { converter ->
                converter.supportedType(
                    requestData.returnTypeData, true
                )
            }?.let {
                return it.wrapSuspendResponse<PRequest>(
                    typeData = requestData.returnTypeData,
                    requestFunction = {
                        Pair(typeInfo<PRequest>(), response)
                    }, ktorfit
                ) as ReturnType
            }

            return response.body<ReturnType>()

        } catch (exception: Exception) {
            val typeIsNullable = requestData.returnTypeData.qualifiedName.endsWith("?")
            return if (typeIsNullable) {
                null
            } else {
                throw exception
            }
        }
    }

    fun HttpRequestBuilder.requestBuilder(
        requestData: RequestData
    ) {

        handleHeaders(requestData.headers)
        handleFields(requestData.fields)
        handleParts(requestData.parts)
        this.method = HttpMethod.parse(requestData.method)

        handleBody(requestData.bodyData)
        handleQueries(requestData)
        val queryNameUrl = handleQueryNames(requestData)

        val relativeUrl = getRelativeUrl(requestData.paths, requestData.relativeUrl)

        val requestUrl = getRequestUrl(ktorfit.baseUrl, relativeUrl, queryNameUrl)

        url(requestUrl)

        requestData.requestBuilder(this)
    }

    private fun getRequestUrl(
        baseUrl: String,
        relativeUrl: String,
        queryNameUrl: String
    ): String {
        return if (relativeUrl.startsWith("http")) {
            relativeUrl
        } else {
            baseUrl + relativeUrl
        } + queryNameUrl
    }

    private fun HttpRequestBuilder.handleBody(body: Any?) {
        body?.let {
            setBody(it)
        }
    }

    /**
     * This method replaces all parts of the [relativeUrl] which have curly braces
     * with their corresponding value
     * @return the relative URL with replaced values
     */
    private fun getRelativeUrl(paths: List<PathData>, relativeUrl: String): String {
        var newUrl = relativeUrl
        paths.forEach {

            val newPathValue = if (it.encoded) {
                it.value
            } else {
                encode(it.value)
            }

            newUrl = newUrl.replace("{${it.key}}", newPathValue)
        }

        return newUrl
    }

    private fun HttpRequestBuilder.handleHeaders(headers: List<HeaderData>) {
        headers {
            headers.forEach {
                when (val data = it.value) {
                    is List<*> -> {
                        data.filterNotNull().forEach { dataEntry ->
                            append(it.key, dataEntry.toString())
                        }
                    }

                    is Array<*> -> {
                        data.filterNotNull().forEach { dataEntry ->
                            append(it.key, dataEntry.toString())
                        }
                    }

                    is Map<*, *> -> {
                        for ((key, value) in data.entries) {
                            append(key.toString(), value.toString())
                        }
                    }

                    null -> {
                        //Ignore this header
                    }

                    else -> {
                        append(it.key, it.value.toString())
                    }
                }
            }
        }
    }

    private fun HttpRequestBuilder.handleFields(fields: List<FieldData>) {
        if (fields.isNotEmpty()) {
            val formParameters = Parameters.build {

                fun append(encoded: Boolean, key: String, value: String) {
                    if (encoded) {
                        append(key, value)
                    } else {
                        append(encode(key), encode(value))
                    }
                }

                fields.filter { it.type == FieldType.FIELD }.forEach { entry ->

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

                        null -> {
                            //Ignore this
                        }

                        else -> {
                            append(entry.encoded, entry.key, entry.data.toString())
                        }
                    }
                }

                fields.filter { it.type == FieldType.FIELDMAP }.forEach { entry ->
                    for ((key, value) in entry.data as Map<*, *>) {
                        value?.let {
                            append(entry.encoded, key.toString(), value.toString())
                        }
                    }
                }
            }
            setBody(FormDataContent(formParameters))

        }
    }

    private fun HttpRequestBuilder.handleQueries(requestData: RequestData) {
        requestData.queries.filter { it.type == QueryType.QUERY }.forEach { entry ->

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

                null -> {
                    //Ignore this query
                }

                else -> {
                    setParameter(entry.encoded, entry.key, entry.data.toString())
                }
            }
        }

        requestData.queries.filter { it.type == QueryType.QUERYMAP }.forEach { entry ->
            for ((key, value) in entry.data as Map<*, *>) {
                value?.let {
                    setParameter(entry.encoded, key.toString(), value.toString())
                }

            }
        }

    }

    /**
     * Returns a String for the query names that will be appended to the url
     * QueryNames will be handled special because otherwise Ktor always adds a "=" behind every
     * query e.g. QueryName("Hello") will be sent by Ktor like "?Hello="
     */
    private fun handleQueryNames(requestData: RequestData): String {
        val queryNames = mutableListOf<String>()
        requestData.queries.filter { it.type == QueryType.QUERYNAME }.forEach { entry ->
            when (val data = entry.data) {
                is List<*> -> {
                    data.filterNotNull().forEach { dataEntry ->
                        if (entry.encoded) {
                            queryNames.add(dataEntry.toString())
                        } else {
                            queryNames.add(encode(dataEntry.toString()))
                        }
                    }
                }

                is Array<*> -> {
                    data.filterNotNull().forEach { dataEntry ->
                        if (entry.encoded) {
                            queryNames.add(dataEntry.toString())
                        } else {
                            queryNames.add(encode(dataEntry.toString()))
                        }
                    }
                }

                null -> {
                    //Ignore this query
                }

                else -> {
                    if (entry.encoded) {
                        queryNames.add(entry.data.toString())
                    } else {
                        queryNames.add(encode(entry.data.toString()))
                    }
                }
            }
        }
        var queryNameUrl = queryNames.joinToString("&") { it }
        queryNameUrl = ("?$queryNameUrl").takeIf { queryNameUrl.isNotEmpty() } ?: ""

        return queryNameUrl
    }


    @OptIn(InternalAPI::class)
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
                    this@formData.append(it.key, it.value)
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
            encodedParameter(key, value)
        } else {
            parameter(key, (value))
        }
    }


    private fun HttpRequestBuilder.encodedParameter(key: String, value: Any): Unit =
        value.let { url.encodedParameters.append(key, it.toString()) } ?: Unit

}

