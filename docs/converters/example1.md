Let's say you want to get an user from an API and the response you get looks like below:

```kotlin title="API response"
{
    "success": true,
    "user":
    {
        "id": 1,
        "name": "Jens Klingenberg"
    }
}
```

But you are only interested in the "user" object, and you want to look your interface function something like this:

```kotlin title="Example function"
@GET("/user")
suspend fun getUser(): User
```

First you need the Kotlin classes to which your JSON data is mapped to:
!!! note "This example assumes that you are Kotlin Serialization"

```kotlin
@kotlinx.serialization.Serializable
data class Envelope(val success: Boolean, val user: User)

@kotlinx.serialization.Serializable
data class User(val id: Int, val name: String)
```


Now you need a converter that can convert the HTTPResponse and return a user object.
Create a class that extends Converter.Factory

```kotlin
class UserFactory : Converter.Factory {

}
```

Because in this case **User** is the return type of a suspend function, you need to create a **SuspendResponseConverter**. Override **suspendResponseConverter()**

```kotlin
class UserFactory : Converter.Factory {
    override fun suspendResponseConverter(
        typeData: TypeData,
        ktorfit: Ktorfit
    ): Converter.SuspendResponseConverter<HttpResponse, *>? {

    }
}
```

Inside **suspendResponseConverter** you can decide if you want to return a converter. In our case we a converter for the
type User.
We can check that case with the typeData that we get as a parameter.

```kotlin
override fun suspendResponseConverter(
    typeData: TypeData,
    ktorfit: Ktorfit
): Converter.SuspendResponseConverter<HttpResponse, *>? {
    if (typeData.typeInfo.type == User::class) {
        ...
    }
    return null
}
```

Next we create the SuspendResponseConverter:
```kotlin
if (typeData.typeInfo.type == User::class) {
    return object : Converter.SuspendResponseConverter<HttpResponse, Any> {
        override suspend fun convert(result: KtorfitResult): Any {
           ...
        }
    }
}

```
Inside of **convert** we get the KtorfitResult and we want to return a User object.

Now we could do the following:

When we know that this converter will always be used for a API that wraps the User inside an Envelope class, we can directly transform the body to an envelope object and just return the user object.

```kotlin
override suspend fun convert(result: KtorfitResult): Any {
    val envelope = result.response.body<Envelope>()
    return envelope.user
}
```

or we can create a TypeData of Envelope and use **nextSuspendResponseConverter()** to look up the next converter that can convert the response

```kotlin
 override suspend fun convert(result: KtorfitResult): Any {
    val typeData = TypeData.createTypeData("com.example.model.Envelope", typeInfo<Envelope>())
    val envelope = ktorfit.nextSuspendResponseConverter(null, typeData)?.convert(result) as? Envelope
    return envelope.user
}
```

Finally, add your converter factory to the Ktorfit Builder 

```kotlin
Ktorfit.Builder().converterFactories(UserFactory()).baseUrl("foo").build()
```