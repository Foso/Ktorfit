<h1>Ktorfit</h1>

[![All Contribtors](https://img.shields.io/badge/Maven-Central-download.svg?style=flat-square)](https://mvnrepository.com/artifact/de.jensklingenberg.ktorfit)
[![jCenter](https://img.shields.io/badge/Apache-2.0-green.svg)](https://github.com/Foso/Ktorfit/blob/master/LICENSE)

<p align="center">
  <img src ="https://raw.githubusercontent.com/Foso/Experimental/master/carbon.png"  />
</p>

# Introduction
Ktorfit is a HTTP client/Kotlin Symbol Processor for Kotlin Multiplatform (Js, Jvm, Android, iOS, Linux) using [KSP](https://github.com/google/ksp) and [Ktor clients](https://ktor.io/docs/getting-started-ktor-client.html) inspired by [Retrofit](https://square.github.io/retrofit/)

## Show some :heart: and star the repo to support the project

[![GitHub stars](https://img.shields.io/github/stars/Foso/Ktorfit.svg?style=social&label=Star)](https://github.com/Foso/Ktorfit) [![GitHub forks](https://img.shields.io/github/forks/Foso/Ktorfit.svg?style=social&label=Fork)](https://github.com/Foso/Ktorfit/fork) [![GitHub watchers](https://img.shields.io/github/watchers/Foso/Ktorfit.svg?style=social&label=Watch)](https://github.com/Foso/Ktorfit) [![Twitter Follow](https://img.shields.io/twitter/follow/jklingenberg_.svg?style=social)](https://twitter.com/jklingenberg_)


## How to use
First do the [Setup](#setup)

Let's say you want to make a GET Request to https://swapi.dev/api/people/1/

Create a new Kotlin interface

```kotlin
interface ExampleApi {
    @GET("people/1/")
    suspend fun getPerson(): String
}
```

!!! info

    The return type String will return the response text. When you want directly parse the response into a class you need to add a JSON,XML, etc. converter to Ktor

Now we add a function that will be used to make our request. The @GET annotation will tell Ktorfit that this a GET request. The value of @GET is the relative URL path that will be appended to the base url which we set later.

An interface used for Ktorfit needs to have a Http method annotation on every function.
Because Ktor relies on Coroutines by default your functions need to have the **suspend** modifier. Alternatively you can use [#Flow](#flow) or Call (see below)


```kotlin
val ktorfit = Ktorfit.Builder().baseUrl("https://swapi.dev/api/").build()
val exampleApi = ktorfit.create<ExampleApi>()
```

Next we need to create a Ktorfit object, in the constructor we set the base url.
We can then use the **create()** function to receive an implementation of the wanted type.

```kotlin
val response = exampleApi.getPerson()
println(response)
```

Now we can use exampleApi to make the request.

## HTTP Request
Ktorfit supports the following the Http Method Annotations:
@GET, @POST, @PUT, @DELETE, @HEAD, @OPTIONS, @PATCH

Or you can set your custom method to @HTTP


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

##ResponseConverter

The idea of a ResponseConverter is to enable directly wrapping response types into other data holder types. 

You can add adapters on your Ktorfit object.

```kotlin
ktorfit.responseConverter(FlowResponseConverter())
```

###Flow
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
ktorfit.responseConverter(KtorfitResponseConverter())
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
You can also add your own Converter. You just need to implement ResponseConverter

```kotlin
class OwnResponseConverter : ResponseConverter {
   ...
```

### JSON
Ktorfit doesn't parse JSON. You have to install the Json Feature to the Ktor Client that you add to Ktorfit.
See here : https://ktor.io/docs/serialization-client.html

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



## Setup
(You can also look how it's done in the [examples](https://github.com/Foso/Ktorfit/tree/master/example))
For Kotlin Native Targets (iOS,Linux) you need to enable the new memory model in gradle.properties

```kotlin
kotlin.native.binary.memoryModel=experimental
```

#### KSP
When you are not using KSP already you need to apply the plugin in your build.gradle
```kotlin

plugins {
  id("com.google.devtools.ksp") version "1.7.0-1.0.6"
}
```

Next you have to add the Ktorfit KSP Plugin to the common target and every compilation target, where you want to use Ktorfit.


```kotlin
val ktorfitVersion = "1.0.0-beta12"

dependencies {
    add("kspCommonMainMetadata", "de.jensklingenberg.ktorfit:ktorfit-ksp:$ktorfitVersion")
    add("ksp[NAMEOFPLATFORM]","de.jensklingenberg.ktorfit:ktorfit-ksp:$ktorfitVersion")
        ...
}
```

[NAMEOFPLATFORM] is the name of the compilation target. When you want to use it for the Android module it's **kspAndroid**, for Js it's **kspJs**, etc.
Look here for more information https://kotlinlang.org/docs/ksp-multiplatform.html


#### Ktorfit-lib

Add the Ktorfit-lib to your common module.
```kotlin
val ktorfitVersion = "1.0.0-beta12"

sourceSets {
    val commonMain by getting{
        dependencies{
            implementation("de.jensklingenberg.ktorfit:ktorfit-lib:$ktorfitVersion")
        }
    }
```

#### Ktor
Ktorfit is based on Ktor Clients 2.1.0. You don't need to add an extra dependency for the default clients. 
When you want to use Ktor plugins for things like serialization, you need to add the dependencies and they need to be compatible with 2.0.1


## Contributions
When you find unexpected behaviour please write an [issue](https://github.com/Foso/Ktorfit/issues/new/choose)
