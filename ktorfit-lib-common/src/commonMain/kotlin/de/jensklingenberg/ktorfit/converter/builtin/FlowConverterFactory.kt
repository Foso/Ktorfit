package de.jensklingenberg.ktorfit.converter.builtin

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.Converter
import de.jensklingenberg.ktorfit.internal.TypeData
import io.ktor.client.statement.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

public class FlowConverterFactory : Converter.Factory {

    override fun responseConverter(
        typeData: TypeData,
        ktorfit: Ktorfit
    ): Converter.ResponseConverter<HttpResponse, *>? {

        if (typeData.typeInfo.type == Flow::class) {

            return object : Converter.ResponseConverter<HttpResponse, Flow<Any>> {
                override fun convert(getResponse: suspend () -> HttpResponse): Flow<Any> {
                    val requestType = typeData.typeArgs.first()
                    return flow {
                        try {
                            val response = getResponse()
                            if (requestType.typeInfo.type == HttpResponse::class) {
                                emit(response)
                            } else {
                                val data = ktorfit.nextSuspendResponseConverter(this@FlowConverterFactory, requestType)
                                    ?.convert(response)
                                emit(data!!)
                            }
                        } catch (exception: Exception) {
                            throw exception
                        }
                    }
                }
            }
        }
        return null
    }
}