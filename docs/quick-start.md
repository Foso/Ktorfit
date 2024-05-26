First do the [Installation](./installation.md)

Let's say you want to make a GET Request to https://swapi.dev/api/people/1/

Create a new Kotlin interface:

```kotlin
interface ExampleApi {
    @GET("people/1/")
    suspend fun getPerson(): String
}
```

Now we add a function that will be used to make our request. The @GET annotation will tell Ktorfit that this a GET request. The value of @GET is the relative URL path that will be appended to the base url which we set later.

An interface used for Ktorfit needs to have a HTTP method annotation on every function.
Because Ktor relies on Coroutines by default your functions need to have the **suspend** modifier. Alternatively you can use [#Flow](../converters/responseconverter#flow) or [Call](../converters/responseconverter#call)

!!! info

    The return type String will return the response text. When you want directly parse the response into a class you need to add a JSON,XML, etc. converter to Ktor

```kotlin
val ktorfit = Ktorfit.Builder().baseUrl("https://swapi.dev/api/").build()
val exampleApi = ktorfit.createExampleApi()
```

Next we use the Ktorfit builder to create a Ktorfit instance, and set the base url.
After compiling the project we can then use the generated extension function to receive an implementation of the wanted type.

```kotlin
val response = exampleApi.getPerson()
println(response)
```

Now we can use exampleApi to make the request.