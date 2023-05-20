package de.jensklingenberg.ktorfit.internal

import io.ktor.client.request.*
import io.ktor.util.reflect.*
import kotlin.reflect.KClass

/**
 * This class is used by the generated code to pass the request information to [KtorfitClient]
 */
@InternalKtorfitApi
public data class RequestData(
    val ktorfitRequestBuilder: HttpRequestBuilder.() -> Unit = {},
    val returnTypeName: String,
    val returnTypeInfo: TypeInfo
) {

    internal fun getTypeData(): TypeData = createTypeData(returnTypeName, returnTypeInfo)
    internal fun createTypeData(qualifiedTypename: String, typeInfo: TypeInfo): TypeData {

        val typeArgument = qualifiedTypename.substringAfter("<").substringBeforeLast(">")
        val spilt = typeArgument.split(",")
        val args = mutableListOf<TypeData>()
        typeInfo.kotlinType?.arguments?.forEachIndexed { index, kTypeProjection ->
            val cleaned = spilt[index].trim()

            val modelKType = kTypeProjection.type
            val modelClass = (modelKType?.classifier as? KClass<*>?)!!

            args.add(createTypeData(cleaned, TypeInfo(modelClass, modelKType.platformType, modelKType)))
        }

        return TypeData(qualifiedTypename.substringBefore("<"), args, typeInfo = typeInfo)
    }
}
