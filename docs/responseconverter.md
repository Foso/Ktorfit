!!! warning "ResponseConverter is deprecated, use [Converter.ResponseConverter](./converters/responseconverter.md) instead"

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

To enable that, you have to implement a ResponseConverter. This class will be used to wrap the Ktor response
inside your wrapper class.

```kotlin
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
```

You can then add the ResponseConverter on your Ktorfit object.

```kotlin
ktorfit.responseConverter(MyOwnResponseConverter())
```

Now add MyOwnResponse to your function
```kotlin
@GET("posts/{postId}/comments")
suspend fun getCommentsByPostId(@Path("postId") postId: Int): MyOwnResponse<List<Comment>>
```
