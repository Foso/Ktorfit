package de.jensklingenberg.ktorfit.adapter

import de.jensklingenberg.ktorfit.Call
import de.jensklingenberg.ktorfit.Callback
import io.ktor.client.statement.*
import io.ktor.util.reflect.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Converter to enable the use of Call<> as return type
 */
class KtorfitCallResponseConverter : SuspendResponseConverter {

    override fun supportedType(returnTypeName: String): Boolean {
        return returnTypeName == "de.jensklingenberg.ktorfit.Call"
    }

    override suspend fun <T : Any> wrapResponse(
        returnTypeName: String,
        requestFunction: suspend () -> Pair<TypeInfo, HttpResponse>
    ): Any {
        val deferredResponse = coroutineScope { async { requestFunction() } }

        val (data, response) = deferredResponse.await()

        val res = runCatching {
            response.call.body(data)
        }

        return object : Call<T> {
            override fun onExecute(callBack: Callback<T>) {
                try {
                    callBack.onResponse(res.getOrThrow() as T, response)
                } catch (ex: Throwable) {
                    callBack.onError(ex)
                }
            }

        }
    }
}

