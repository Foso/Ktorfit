package de.jensklingenberg.ktorfit.internal

import io.ktor.client.request.*
import io.ktor.util.reflect.*

/**
 * @param returnTypeData This is the qualifiedName of requested return type
 * It will be used by [ResponseConverter] to check if they support the type
 * Because on JS the qualifiedName reflection does not exist, it is inserted as arguments by the Compiler Plugin
 */
public data class RequestData(
    val returnTypeData: TypeData,
    val requestTypeInfo: TypeInfo,
    val returnTypeInfo: TypeInfo,
    val ktorfitRequestBuilder: HttpRequestBuilder.() -> Unit = {},
)
