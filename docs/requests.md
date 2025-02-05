## HTTP Request
Ktorfit supports the following the HTTP method annotations:

* @GET
* @POST
* @PUT
* @DELETE
* @HEAD
* @OPTIONS
* @PATCH

Or you can set your custom method to @HTTP


```kotlin
@GET("posts")
fun getPosts(): List<Post>
```

The value of the HTTP annotation will be appended to the **baseUrl** that you set in the Ktorfit builder.
If the value contains a url that starts with http or https, this url will be used for the request instead of the baseUrl.

```kotlin
@GET("https://example.com/posts")
fun getPosts(): List<Post>
```

The value can only be empty when you also use [@Url](#url)

## Url
Can be used to set a URL dynamically as a function parameter.
```kotlin
@GET("")
suspend fun getPosts(@Url url: String): List<Post>
```

## Query
```kotlin
@GET("comments")
suspend fun getCommentsById(
    @Query("postId") postId: String,
    @QueryName queryName: String,
    @QueryMap headerMap : Map<String,String>
): List<Comment>
```


You can use @Query, @QueryName or @QueryMap to set queries to your request.

Example:
```kotlin
@GET("comments")
suspend fun getCommentsById(@Query("postId") postId: String): List<Comment>
```

A request with **getCommentsById(3)** will result in the relative URL "comments?postId=3"

## Path

When you want to dynamically replace a part of the URL, you can use the @Path annotation.
```kotlin
interface ExampleApi {
    @GET("people/{peopleId}/")
    suspend fun getPerson(@Path("peopleId") id: String): String
}
```
Just write a part of your URL path in curly braces. Then you need to annotate a parameter with @Path.
The value of @Path needs to match with one of the curly braces part in your URL path.

Example:

On a request with **getPerson(1)** , **{peopleId}** will be replaced with the argument **1** and the relative URL will become
**"people/1/"**

## Headers
```kotlin
@Headers("Accept: application/json")
@GET("comments")
suspend fun requestWithHeaders(
    @Header("Content-Type") name: String,
    @HeaderMap headerMap : Map<String,String>
): List<Comment>
```

You can use @Headers, @Header or @HeaderMap to configure headers to your request.

## Body

```kotlin
interface ExampleService {
    @POST("upload")
    suspend fun upload(@Body data: String)
}
```

@Body can be used as parameter to send data in a request body.
It can only be used with Http Methods that have a request body

## FormData
```kotlin
@POST("signup")
@FormUrlEncoded
suspend fun signup(
@Field("username") username: String, @Field("email") email: String,
@Field("password") password: String, @Field("confirmation") confirmation: String
): String
```

To send FormData you can use @Field or @FieldMap. Your function needs to be annotated with @FormUrlEncoded.

## Tag
Tag can be used to add a tag to a request.

```kotlin
@GET("posts")
fun getPosts(@Tag("myTag") tag: String): List<Post>
```

You can then access the tag from the attributes of a Ktor HttpClientCall

```kotlin
val myTag = response.call.attributes[AttributeKey("myTag")] 
```

## Multipart
To send Multipart data you have two options:

### 1) @Body
```kotlin
interface ExampleService {
    @POST("upload")
    suspend fun upload(@Body map: MultiPartFormDataContent)
}
```

To upload MultiPartFormData you need to have a parameter of the type MultiPartFormDataContent that is annotated with @Body. The method needs to be annotated with @POST or @PUT

```kotlin
val multipart = MultiPartFormDataContent(formData {
    append("description", "Ktor logo")
    append("image", File("ktor_logo.png").readBytes(), Headers.build {
        append(HttpHeaders.ContentType, "image/png")
        append(HttpHeaders.ContentDisposition, "filename=ktor_logo.png")
    })
})

exampleApi.upload(multipart)
```

Then you can use Ktor's formData Builder to create the MultiPartFormDataContent

### 2) @MultiPart
```kotlin
@Multipart
@POST("upload")
suspend fun uploadFile(@Part("description") description: String, @Part("") file: List<PartData>): String
```

You can annotate a function with @Multipart. Then you can annotate parameters with @Part

```kotlin
val multipart = formData {
    append("image", File("ktor_logo.png").readBytes(), Headers.build {
        append(HttpHeaders.ContentType, "image/png")
        append(HttpHeaders.ContentDisposition, "filename=ktor_logo.png")
    })
}

exampleApi.upload("Ktor logo",multipart)
```


All your parameters annotated with @Part wil be combined and send as MultiPartFormDataContent


## JSON
Ktorfit doesn't parse JSON. You have to install the Json Feature to the Ktor Client that you add to Ktorfit.

See here [Add your own Ktor client](../configuration/#add-your-own-ktor-client) and here https://ktor.io/docs/serialization-client.html

```kotlin
val ktorClient = HttpClient() {
        install(ContentNegotiation) {
             json(Json { isLenient = true; ignoreUnknownKeys = true })
        }
}
```

## Streaming

```kotlin
@Streaming
@GET("docs/response.html#streaming")
suspend fun getPostsStreaming(): HttpStatement
```

To receive streaming data you need to annotate a function with @Streaming and the return type has to be HttpStatement.


```kotlin
exampleApi.getPostsStreaming().execute { response ->
        //Do something with response
}
```

For more information check [Ktor docs](https://ktor.io/docs/response.html#streaming)

## RequestBuilder
```kotlin
@GET("comments")
suspend fun getCommentsById(
    @Query("postId") name: String,
    @ReqBuilder ext: HttpRequestBuilder.() -> Unit
): List<Comment>
```

You need to set extra configuration on your request? Add a parameter with "@ReqBuilder ext: HttpRequestBuilder.() -> Unit" to your function.
```kotlin
val result = secondApi.getCommentsById("3") {
    onDownload { bytesSentTotal, contentLength ->
        println(bytesSentTotal)
    }
}
```

Then you can use the extension function to set additional configuration. The RequestBuilder will be applied last after everything that is set by Ktorfit

## Annotations
Function annotations are available in the request object with their respective values via the `annotation` extension (`HttpRequest.annotations` or `HttpRequestBuilder.annotations`)

Do note that `OptIn` annotation is not included in the returned list

```kotlin
@AuthRequired(optional = true)
@POST("comments")
suspend fun postComment(
    @Query("issue") issue: String,
    @Query("message") message: String,
): List<Comment>
```

```kotlin
val MyAuthPlugin = createClientPlugin("MyAuthPlugin", ::MyAuthPluginConfig) {
    onRequest { request, _ ->
        val auth = request.annotations.filterIsInstance<AuthRequired>().firstOrNull() ?: return@onRequest

        val token  = this@createClientPlugin.pluginConfig.token
        if (!auth.optional && token == null) throw Exception("Need to be logged in")

        token?.let { request.headers.append("Authorization", "Bearer $it") }

    }
}

class MyAuthPluginConfig {
    var token: String? = null
}
```
