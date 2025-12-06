# How Ktorfit works under the hood

Ktorfit consists of three main components KSP-Plugin, Compiler plugin and the Ktorfit lib

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

```kotlin
@OptIn(InternalKtorfitApi::class)
public class _ExampleApiImpl(
    private val _baseUrl: String,
    private val _helper: KtorfitConverterHelper,
) : ExampleApi {
    
    override suspend fun exampleGet(): People {
        val _ext: HttpRequestBuilder.() -> Unit = {
            method = HttpMethod.parse("GET")
            url{
                takeFrom(_baseUrl + "/test")
            }
        }
        val _typeData = TypeData.createTypeData(
            typeInfo = typeInfo<People>(),
        )
        return _helper.suspendRequest(_typeData,_ext)!!
    }
}

public class _ExampleApiProvider : ClassProvider<ExampleApi> {
    override fun create(_ktorfit: Ktorfit): ExampleApi = _ExampleApiImpl(_ktorfit.baseUrl, KtorfitConverterHelper(_ktorfit))
}

public fun Ktorfit.createExampleApi(): ExampleApi = _ExampleApiImpl(this.baseUrl, KtorfitConverterHelper(this))

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
val api = jvmKtorfit.create<ExampleApi>(_ExampleApiImpl(jvmKtorfit))
```

The create() function is used, checks that the compiler plugin replaced the default value

```kotlin
public fun <T> create(data: T? = null): T {
    if (data == null) {
        throw IllegalArgumentException(ENABLE_GRADLE_PLUGIN)
    }
    return data
}
```
