
Let`s say you have a function that requests a list of comments

```kotlin
@GET("posts/{postId}/comments")
suspend fun getCommentsByPostId(@Path("postId") postId: Int): List<Comment>
```

But now you want to directly wrap your comment list in your data holder class e.g. "MyOwnResponse"

```kotlin
sealed class MyOwnResponse<T> {
    data class Success<T>(val data: T) : MyOwnResponse<T>()
    class Error(val ex:Throwable) : MyOwnResponse<Nothing>()

    companion object {
        fun <T> success(data: T) = Success(data)
        fun error(ex: Throwable) = Error(ex)
    }
}
```

To enable that, you have to implement a SuspendResponseConverter. This class will be used to wrap the Ktor response
inside your wrapper class.

```kotlin
class MyOwnResponseConverterFactory : Converter.Factory{

    override fun suspendResponseConverter(
        typeData: TypeData,
        ktorfit: Ktorfit
    ): Converter.SuspendResponseConverter<HttpResponse, *>? {
        if(typeData.typeInfo.type == MyOwnResponse::class) {
           
            return object : Converter.SuspendResponseConverter<HttpResponse, Any> {
                override suspend fun convert(response: HttpResponse): Any {
                    return try {
                        MyOwnResponse.success(response.body(typeData.typeArgs.first().typeInfo))
                    } catch (ex: Throwable) {
                        MyOwnResponse.error(ex)
                    }
                }
            }
        }
        return null
    }
}
```

You can then add the ResponseConverter to your Ktorfit object.

```kotlin
ktorfit.converterFactories(MyOwnResponseConverterFactory())
```

Now add MyOwnResponse to your function
```kotlin
@GET("posts/{postId}/comments")
suspend fun getCommentsByPostId(@Path("postId") postId: Int): MyOwnResponse<List<Comment>>
```
