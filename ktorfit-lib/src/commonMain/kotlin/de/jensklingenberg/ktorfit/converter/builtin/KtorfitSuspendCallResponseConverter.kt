package de.jensklingenberg.ktorfit.converter.builtin

import de.jensklingenberg.ktorfit.Call
import de.jensklingenberg.ktorfit.Callback
import de.jensklingenberg.ktorfit.converter.SuspendResponseConverter
import io.ktor.client.statement.*
import io.ktor.util.reflect.*
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

/**
 * Converter to enable the use of Call<> as return type in suspend functions
 * e.g. suspend fun test(): Call<String>
 */
class KtorfitSuspendCallResponseConverter : SuspendResponseConverter {

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

