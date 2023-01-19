# How Ktorfit works under the hood

## KSP-Plugin 
This will generate the code for the implementation of the interfaces

## Compiler plugin
This transforms the create() function from the Ktorfit lib

## Ktorfit lib
A wrapper around Ktor to simplify code generation

## Example 
```kotlin
package com.example

import com.example.model.People
import de.jensklingenberg.ktorfit.http.GET

interface ExampleApi  {
    @GET("/test")
    suspend fun exampleGet(): People
}
```
Let`s say we have a interface like this.

At compile time Ktorfit/KSP checks for all functions that are annotated with Ktorfit annotations like @GET.

Then it looks at the parent interfaces of that functions and generates, the source code of a Kotlin class that implements the interface. The classes are named like the interfaces but with an underscore at the beginning and "Impl" at the end, and they have the same package as the interfaces. In this case a class named _ExampleApiImpl will be generated.
The class will also implement **KtorfitService**. The setClient() function will be used to add the http client at runtime.

```kotlin
@OptIn(InternalKtorfitApi::class)
public class _ExampleApiImpl : ExampleApi, KtorfitService {
    private lateinit var client: KtorfitClient

    public override suspend fun exampleGet(): People {
        val requestData = RequestData(method="GET",
            relativeUrl="/test",
            returnTypeData=TypeData("com.example.model.People"))

        return client.suspendRequest<People, People>(requestData)!!
    }

    public override fun setClient(client: KtorfitClient): Unit {
        this.client = client
    }
}

@OptIn(InternalKtorfitApi::class)
public fun Ktorfit.createExampleApi(): ExampleApi = _ExampleApiImpl().also{
    it.setClient(KtorfitClient(this)) }
```

The next part is the compiler plugin which is added by the gradle plugin.
It looks for the every usage of the create function from the Ktorfit-lib and adds an object of the 
wanted implementation class as an argument. Because of the naming convention of the generated classes
we can deduce the name of the class from the name of type parameter.

```kotlin
val api = jvmKtorfit.create<ExampleApi>()
```

will be transformed to: 

```kotlin
val api = jvmKtorfit.create<ExampleApi>(_ExampleApiImpl())
```

When the create() function is used, the object is cast to a KtorfitService and the client will be added.
Then it is cast to requested type <T>

```kotlin
fun <T> create(ktorfitService: KtorfitService = DefaultKtorfitService()): T {
        if (ktorfitService is DefaultKtorfitService) {
            throw IllegalArgumentException("You need to enable the Ktorfit Gradle Plugin")
        }
        ktorfitService.setClient(KtorfitClient(this))
        return ktorfitService as T
    }
```
