package de.jensklingenberg.ktorfit.adapter

import de.jensklingenberg.ktorfit.Call
import de.jensklingenberg.ktorfit.Callback
import io.ktor.client.statement.*
import io.ktor.util.reflect.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * Converter to enable the use of Call<> as return type
 */
class KtorfitCallResponseConverter : ResponseConverter {

    override fun supportedType(returnTypeName: String): Boolean {
        return returnTypeName == "de.jensklingenberg.ktorfit.Call"
    }

    override fun <T : Any> wrapResponse(
        returnTypeName: String,
        requestFunction: suspend () -> Pair<TypeInfo, HttpResponse>
    ): Any {
        return object : Call<T> {
            override fun onExecute(callBack: Callback<T>) {
                GlobalScope.launch {
                    val deferredResponse = async { requestFunction() }

                    val (data, response) = deferredResponse.await()

                    try {
                        val res = response.call.body(data)
                        callBack.onResponse(res as T, response)
                    } catch (ex: Exception) {
                        callBack.onError(ex)
                    }

                }
            }

        }
    }
}

