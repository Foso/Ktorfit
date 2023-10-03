package com.example.model

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.Converter
import de.jensklingenberg.ktorfit.converter.SuspendResponseConverter
import de.jensklingenberg.ktorfit.internal.TypeData
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.util.reflect.*

class MyOwnResponseConverter : SuspendResponseConverter {

    override suspend fun <RequestType> wrapSuspendResponse(
        typeData: TypeData,
        requestFunction: suspend () -> Pair<TypeInfo, HttpResponse>,
        ktorfit: Ktorfit
    ): Any {
        return try {
            val (info, response) = requestFunction()
            MyOwnResponse.success<Any>(response.body(info))
        } catch (ex: Throwable) {
            MyOwnResponse.error(ex)
        }
    }

    override fun supportedType(typeData: TypeData, isSuspend: Boolean): Boolean {
        return typeData.qualifiedName == "com.example.model.MyOwnResponse"
    }
}

class MyOwnResponseConverterFactory : Converter.Factory {

    override fun suspendResponseConverter(
        typeData: TypeData,
        ktorfit: Ktorfit
    ): Converter.SuspendResponseConverter<HttpResponse, *>? {
        if (typeData.typeInfo.type == MyOwnResponse::class) {
            return object : Converter.SuspendResponseConverter<HttpResponse, Any> {
                override suspend fun convert(response: HttpResponse): Any {
                    return try {
                        val data = response.call.body(typeData.typeArgs.first().typeInfo)
                        MyOwnResponse.success(data)
                    } catch (ex: Throwable) {
                        MyOwnResponse.error(ex)
                    }
                }
            }
        }
        return null
    }
}