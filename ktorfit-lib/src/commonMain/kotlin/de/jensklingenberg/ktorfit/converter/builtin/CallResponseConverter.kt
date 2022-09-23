package de.jensklingenberg.ktorfit.converter.builtin

import de.jensklingenberg.ktorfit.Call
import de.jensklingenberg.ktorfit.Callback
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.ResponseConverter
import de.jensklingenberg.ktorfit.internal.TypeData
import io.ktor.client.statement.*
import io.ktor.util.reflect.*
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CallResponseConverter : ResponseConverter {

    override suspend fun <RequestType> wrapResponse(
        typeData: TypeData,
        requestFunction: suspend () -> Pair<TypeInfo, HttpResponse>,
        ktorfit: Ktorfit
    ): Any {
        return object : Call<RequestType> {
            override fun onExecute(callBack: Callback<RequestType>) {

                ktorfit.httpClient.launch {
                    val deferredResponse = async { requestFunction() }

                    val (data, response) = deferredResponse.await()

                    try {
                        val res = response.call.body(data)
                        callBack.onResponse(res as RequestType, response)
                    } catch (ex: Exception) {
                        callBack.onError(ex)
                    }

                }
            }

        }
    }

    override fun supportedType(typeData: TypeData, isSuspend: Boolean): Boolean {
        return typeData.qualifiedName == "de.jensklingenberg.ktorfit.Call"
    }
}