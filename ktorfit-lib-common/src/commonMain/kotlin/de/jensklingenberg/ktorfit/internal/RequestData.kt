package de.jensklingenberg.ktorfit.internal

import io.ktor.client.request.*
import io.ktor.util.reflect.*
import kotlin.reflect.KClass

/**
 * It will be used by [ResponseConverter] to check if they support the type
 * Because on JS the qualifiedName reflection does not exist, it is inserted as arguments by the Compiler Plugin
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
