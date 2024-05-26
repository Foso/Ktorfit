You only need ResponseConverters for cases where you can't use a suspend function in your interface. For every other
case you want [SuspendResponseConverter](./suspendresponseconverter.md)

Because Ktor relies on Coroutines by default your functions need to have the suspend modifier.

Let's say you have API endpoint to get a list of comments and you want to get them as Flow.
!!! note "Ktorfit already has a converter for Flow, but it's used as an example"

```kotlin
@GET("/user")
fun getUser(): Flow<List<Commment>>
```

Now you need a converter that can convert the HTTPResponse and return a Flow.
Create a class that extends Converter.Factory

```kotlin
class FlowConverterFactory : Converter.Factory {
}
```

Next you need to overwrite **responseConverter()**

```kotlin
override fun responseConverter(
    typeData: TypeData,
    ktorfit: Ktorfit
): Converter.ResponseConverter<HttpResponse, *>? {
```

Inside **responseConverter** you can decide if you want to return a converter. In our case we want a converter for the
type Flow.
We can check that case with the typeData that we get as a parameter.

```kotlin
override fun suspendResponseConverter(
    typeData: TypeData,
    ktorfit: Ktorfit
): Converter.SuspendResponseConverter<HttpResponse, *>? {
    if (typeData.typeInfo.type == Flow::class) {
        ...
    }
    return null
}
```

Next we create the ResponseConverter:

```kotlin
if (typeData.typeInfo.type == User::class) {
    val requestType = typeData.typeArgs.first()

    return object : Converter.ResponseConverter<HttpResponse, Flow<Any>> {
        override fun convert(getResponse: suspend () -> HttpResponse): Flow<Any> {
            return flow {
                try {
                    val response = getResponse()
                    if (requestType.typeInfo.type == HttpResponse::class) {
                        emit(response)
                    } else {
                        val data = ktorfit.nextSuspendResponseConverter(this@FlowConverterFactory, requestType)
                            ?.convert(response)
                        emit(data)
                    }
                } catch (exception: Exception) {
                    throw exception
                }
            }
        }
    }
}

```

Inside of **convert** we get the HttpResponse from getResponse(). We use nextSuspendResponseConverter to find the next converter that can 
convert the response. Then we put the converted response in the Flow and return it.

Finally, add your converter factory to the Ktorfit Builder

```kotlin
Ktorfit.Builder().converterFactories(FlowConverterFactory()).baseUrl("foo").build()
```

### Flow

Add this dependency:
```kotlin
implementation("de.jensklingenberg.ktorfit:ktorfit-converters-flow:$CONVERTER_VERSION")
```

Ktorfit has support for Kotlin Flow. You need add the FlowConverterFactory() to your Ktorfit instance.

```kotlin
ktorfit.converterFactories(FlowConverterFactory())
```

```kotlin
@GET("comments")
fun getCommentsById(@Query("postId") postId: String): Flow<List<Comment>>
```

Then you can drop the **suspend** modifier and wrap your return type with Flow<>

### Call

Add this dependency:
```kotlin
implementation("de.jensklingenberg.ktorfit:ktorfit-converters-call:$CONVERTER_VERSION")
```

```kotlin
ktorfit.converterFactories(CallConverterFactory())
```

```kotlin
@GET("people/{id}/")
fun getPersonById(@Path("id") peopleId: Int): Call<People>
```

```kotlin
exampleApi.getPersonById(3).onExecute(object : Callback<People> {
    override fun onResponse(call: People, response: HttpResponse) {
        //Do something with Response
    }

    override fun onError(exception: Exception) {
        //Do something with exception
    }
})
```

You can use Call<T> to receive the response in a Callback.
