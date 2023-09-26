package de.jensklingenberg.ktorfit.converter.builtin

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.request.ResponseConverter
import de.jensklingenberg.ktorfit.internal.TypeData
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.util.reflect.*
import kotlinx.coroutines.flow.flow

/**
 * Converter to enable the use of Flow<> as return type
 */
@Deprecated("Use FlowConverterFactory")
public class FlowResponseConverter : ResponseConverter {

    override fun supportedType(typeData: TypeData, isSuspend: Boolean): Boolean {
        return typeData.qualifiedName == "kotlinx.coroutines.flow.Flow"
    }

    override fun <RequestType> wrapResponse(
        typeData: TypeData,
        requestFunction: suspend () -> Pair<TypeInfo, HttpResponse?>,
        ktorfit: Ktorfit
    ): Any {
        return flow<Any> {
            try {
                val (info, response) = requestFunction()
                if (info.type == HttpResponse::class) {
                    emit(response!!)
                } else {
                    emit(response!!.body(info))
                }
            } catch (exception: Exception) {
                throw exception
            }
        }
    }
}


