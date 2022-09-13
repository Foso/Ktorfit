package de.jensklingenberg.ktorfit.demo

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.SuspendResponseConverter
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.util.reflect.*

class SuspendConverter : SuspendResponseConverter {

    override fun supportedType(returnTypeName: String, isSuspend: Boolean): Boolean {
        return returnTypeName.equals("de.jensklingenberg.ktorfit.demo.Response", true)
    }

    override suspend fun <PRequest> wrapSuspendResponse(
        returnTypeName: String,
        requestFunction: suspend () -> Pair<TypeInfo, HttpResponse>,
        ktorfit: Ktorfit
    ): Any {

        return try {
            val (info, response) = requestFunction()
            Response.success<Any>(response.body(info))
        } catch (ex: Throwable) {
            Response.error(ex)
        }
    }
}