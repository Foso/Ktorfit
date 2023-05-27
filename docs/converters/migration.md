* SuspendResponseConverter -> Converter.SuspendResponseConverter

```kotlin title="SuspendResponseConverter"
 override suspend fun <RequestType> wrapSuspendResponse(
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
                } } } } }

override fun supportedType(typeData: TypeData, isSuspend: Boolean): Boolean {
    return typeData.qualifiedName == "de.jensklingenberg.ktorfit.Call"
}
```


```kotlin title="Equivalent with converter factory:"
public class CallConverterFactory : Converter.Factory {

    override fun suspendResponseConverter(
        typeData: TypeData,
        ktorfit: Ktorfit
    ): Converter.SuspendResponseConverter<HttpResponse, *>? {
        if (typeData.typeInfo.type == Call::class) {
            return object: Converter.SuspendResponseConverter<HttpResponse, Call<Any?>> {
                override suspend fun convert(response: HttpResponse): Call<Any?> {

                    return object : Call<Any?> {
                        override fun onExecute(callBack: Callback<Any?>) {
                            ktorfit.httpClient.launch {
                                try {
                                    val data = response.call.body(typeData.typeArgs.first().typeInfo)
                                    callBack.onResponse(data!!, response)
                                } catch (ex: Exception) {
                                    callBack.onError(ex)
                                } } } } } } 
        }
        return null
    }
}
```

* ResponseConverter -> Converter.ResponseConverter

```kotlin title="ResponseConverter"
override fun <RequestType> wrapResponse(
typeData: TypeData,
requestFunction: suspend () -> Pair<TypeInfo, HttpResponse?>,
ktorfit: Ktorfit
): Any {
return object : Call<RequestType> {
override fun onExecute(callBack: Callback<RequestType>) {

                ktorfit.httpClient.launch {
                    val deferredResponse = async { requestFunction() }

                    try {
                        val (info, response) = deferredResponse.await()
                        val data = response!!.body(info) as RequestType
                        callBack.onResponse(data, response)
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
```

```kotlin title="Equivalent with converter factory:"
public class CallConverterFactory : Converter.Factory {
    override fun responseConverter(
        typeData: TypeData,
        ktorfit: Ktorfit
    ): Converter.ResponseConverter<HttpResponse, *>? {
        if (typeData.typeInfo.type == Call::class) {
            return object : Converter.ResponseConverter<HttpResponse, Call<Any?>> {

                override fun convert(getResponse: suspend () -> HttpResponse): Call<Any?> {
                    return object : Call<Any?> {
                        override fun onExecute(callBack: Callback<Any?>) {
                            ktorfit.httpClient.launch {
                                try {
                                    val response = getResponse()

                                    val data = response.call.body(typeData.typeArgs.first().typeInfo)

                                    callBack.onResponse(data, response)
                                } catch (ex: Exception) {
                                    println(ex)
                                    callBack.onError(ex)
                                } } } } } } 
        }
        return null
    }
}
```