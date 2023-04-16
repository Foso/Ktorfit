package de.jensklingenberg.ktorfit.internal

import io.ktor.client.request.*
import io.ktor.util.reflect.*

/**
 * @param method HTTP Keyword of requested Http Method Type
 * @param relativeUrl This is the request relative url path e.g. "posts/1"
 * @param headers list of Headers that will be used for request
 * @param queries list of query parameters that will be added to the requested url
 * @param fields list of fields parameters that will be added to the formData Body
 * @param bodyData data that will send to with the request
 * @param returnTypeData This is the qualifiedName of requested return type
 * It will be used by [ResponseConverter] to check if they support the type
 * Because on JS the qualifiedName reflection does not exist, it is inserted as arguments by the Compiler Plugin
 */
public data class RequestData(
    val method: String,
    val relativeUrl: String,
    val headers: List<DH> = emptyList(),
    val queries: List<DH> = emptyList(),
    val fields: List<DH> = emptyList(),
    val parts: Map<String, Any> = emptyMap(),
    val bodyData: BodyData? = null,
    val returnTypeData: TypeData,
    val requestBuilder: HttpRequestBuilder.() -> Unit = {},
    val paths: List<DH> = emptyList(),
    val requestTypeInfo: TypeInfo,
    val returnTypeInfo: TypeInfo
)

public data class BodyData(val bodyData: Any? = null, val typeInfo: TypeInfo)
