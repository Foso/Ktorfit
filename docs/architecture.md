# How Ktorfit works under the hood

Ktorfit consists of three main components KSP-Plugin, Compiler plugin and the Ktorfit lib

## KSP-Plugin 
This will generate the code for the implementation of the interfaces

## Compiler plugin
This transforms the create() function from the Ktorfit lib

## Ktorfit lib
A wrapper around Ktor to simplify code generation.

## Per-Target Generation

When `perTargetGeneration = true` is set in the Ktorfit Gradle plugin config, a different code generation pipeline is used:

- Each KMP target runs KSP independently instead of relying on `kspCommonMainKotlinMetadata`
- This removes the serial bottleneck of the metadata task triggering metadata compilation for all targets from child modules
- Each API interface in `commonMain` requires an `expect` declaration; KSP validates it and generates the `actual` in each platform target
- Multi-module projects are safe because each module has uniquely-named API interfaces

## expect/actual Pattern

When `perTargetGeneration = true`, the generated extension function becomes an `actual` implementation of a user-declared `expect`:

**commonMain (user writes):**
```kotlin
interface ExampleApi {
    @GET("/test")
    suspend fun exampleGet(): People
}

expect fun Ktorfit.createExampleApi(): ExampleApi
```

**jvmMain / jsMain / nativeMain (KSP generates):**
```kotlin
actual fun Ktorfit.createExampleApi(): ExampleApi =
    _ExampleApiImpl(this.baseUrl, this.httpClient, KtorfitConverterHelper(this))
```

This allows calling `ktorfit.createExampleApi()` directly from `commonMain` — the compiler resolves it to the target-specific `actual`. No registry, no class-loading tricks, no runtime failures.

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
    private val _httpClient: HttpClient,
    private val _helper: KtorfitConverterHelper,
) : ExampleApi {
    
    override suspend fun exampleGet(): People {
        val _ext: HttpRequestBuilder.() -> Unit = {
            method = HttpMethod.parse("GET")
            url{
                takeFrom(_baseUrl + "/test")
            }
        }
        return _helper.suspendRequest(_httpClient, _ext, typeInfo<People>(),)!!
    }
}

// Generated in platform-specific source sets when perTargetGeneration=true:
actual fun Ktorfit.createExampleApi(): ExampleApi =
    _ExampleApiImpl(this.baseUrl, this.httpClient, KtorfitConverterHelper(this))
```

When `perTargetGeneration = false` (legacy), the extension function is generated without the `actual` modifier directly into `commonMain`.

### Compiler Plugin Path (legacy)

The compiler plugin looks for every usage of the `create` function from the Ktorfit-lib and adds an object
of the wanted implementation class as an argument.

```kotlin
val api = jvmKtorfit.create<ExampleApi>()
```

will be transformed to:

```kotlin
val api = jvmKtorfit.create<ExampleApi>(_ExampleApiProvider() )
```

When `perTargetGeneration = true`, skip the compiler plugin path entirely — use the extension function directly (see [expect/actual Pattern](#expectactual-pattern) above):

```kotlin
val api = jvmKtorfit.createExampleApi()
```
