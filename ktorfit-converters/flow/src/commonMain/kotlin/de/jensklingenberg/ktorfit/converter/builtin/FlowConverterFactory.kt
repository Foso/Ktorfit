package de.jensklingenberg.ktorfit.converter.builtin

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.Converter
import de.jensklingenberg.ktorfit.converter.TypeData
import io.ktor.client.call.*
import io.ktor.client.statement.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Factory that enables the use of Flow<T> as return type
 */
public class FlowConverterFactory : Converter.Factory {

    override fun responseConverter(
        typeData: TypeData,
        ktorfit: Ktorfit
    ): Converter.ResponseConverter<HttpResponse, *>? {

        if (typeData.typeInfo.type == Flow::class) {

            return object : Converter.ResponseConverter<HttpResponse, Flow<Any?>> {
                override fun convert(getResponse: suspend () -> HttpResponse): Flow<Any?> {
                    val requestType = typeData.typeArgs.first()
                    return flow {
                        val response = getResponse()
                        if (requestType.typeInfo.type == HttpResponse::class) {
                            emit(response)
                        } else {
                            val convertedBody = ktorfit.nextSuspendResponseConverter(
                                this@FlowConverterFactory,
                                typeData.typeArgs.first()
                            )?.convert(response)
                                ?: response.body(typeData.typeArgs.first().typeInfo)
                            emit(convertedBody)
                        }
                    }
                }
            }
        }
        return null
    }
}