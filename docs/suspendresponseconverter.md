!!! warning "SuspendResponseConverter is deprecated, use [Converter.SuspendResponseConverter](./converters/suspendresponseconverter.md) instead"

Because Ktor relies on Coroutines by default your functions need to have the suspend modifier.

To change this, you need to use a SuspendResponseConverter, you add your own or use [Flow](#flow) or [Call](#call)

You can add RequestConverter on your Ktorfit object.

```kotlin
ktorfit.responseConverter(FlowResponseConverter())
```

### Flow
Ktorfit has support for Kotlin Flow. You need add the FlowResponseConverter() to your Ktorfit instance.

```kotlin
ktorfit.responseConverter(FlowResponseConverter())
```

```kotlin

@GET("comments")
fun getCommentsById(@Query("postId") postId: String): Flow<List<Comment>>
```

Then you can drop the **suspend** modifier and wrap your return type with Flow<>


### Call

```kotlin
ktorfit.responseConverter(CallResponseConverter())
```
```kotlin
@GET("people/{id}/")
fun getPersonById(@Path("id") peopleId: Int): Call<People>
```

```kotlin
exampleApi.getPersonById(3).onExecute(object : Callback<People>{
    override fun onResponse(call: People, response: HttpResponse) {
        //Do something with Response
    }

    override fun onError(exception: Exception) {
        //Do something with exception
    }
})
```

You can use Call<T> to receive the response in a Callback.

### Your own
You can also add your own Converter. You just need to implement RequestConverter. Inside the converter you need to handle
the conversion from **suspend** to your async code.

```kotlin
class MyOwnResponseConverter : SuspendResponseConverter {
   ...
```
