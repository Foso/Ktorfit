package de.jensklingenberg.ktorfit.converter.builtin

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.ResponseConverter
import io.ktor.client.statement.*
import io.ktor.util.reflect.*
import kotlinx.coroutines.flow.flow

/**
 * Converter to enable the use of Flow<> as return type
 */
class FlowResponseConverter : ResponseConverter {

    override fun supportedType(returnTypeName: String, isSuspend: Boolean): Boolean {
        return returnTypeName == "kotlinx.coroutines.flow.Flow"
    }

    override fun <PRequest> wrapResponse(
        returnTypeName: String,
        requestFunction: suspend () -> Pair<TypeInfo, HttpResponse>,
        ktorfit: Ktorfit
    ): Any {
        return flow {
            try {
                val (info, response) = requestFunction()
                emit(response.call.body(info))
            } catch (exception: Exception) {
                throw exception
            }
        }
    }
}



