package de.jensklingenberg.ktorfit.internal

import de.jensklingenberg.ktorfit.internal.TypeData.Companion.createTypeData
import io.ktor.client.request.*
import io.ktor.util.reflect.*

/**
 * This class is used by the generated code to pass the request information to [KtorfitClient]
 */
@InternalKtorfitApi
public data class RequestData(
    val ktorfitRequestBuilder: HttpRequestBuilder.() -> Unit = {},
    private val returnTypeName: String,
    private val returnTypeInfo: TypeInfo
) {
    internal fun getTypeData(): TypeData = createTypeData(returnTypeName, returnTypeInfo)
}
