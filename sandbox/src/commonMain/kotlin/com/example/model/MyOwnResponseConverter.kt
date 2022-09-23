package com.example.model

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.ResponseConverter
import de.jensklingenberg.ktorfit.internal.TypeData
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.util.reflect.*

class MyOwnResponseConverter : ResponseConverter {

    override suspend fun <RequestType> wrapResponse(
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