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

## Factory Registry (Optional)

When `enableFactoryRegistry = true` is set in the Ktorfit Gradle plugin config:

- Generated `_<Name>Impl.kt` files include a self-registration property that registers a `ClassProvider` into `KtorfitFactoryRegistry`
- The `Ktorfit.createUsingRegistry<T>()` inline function looks up the registry, removing the need for the compiler plugin
- Usage requires opt-in via `@ExperimentalFactoryRegistry`

This flag is independent of `perTargetGeneration` but works well together with it to simplify API creation from `commonMain`.

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

When `enableFactoryRegistry = true`, a self-registration property is also generated:

```kotlin
@Suppress("unused")
private val __ktorfit_registration: Unit =
    run { KtorfitFactoryRegistry.register(ExampleApi::class, _ExampleApiProvider()) }
```

### Compiler Plugin Path (legacy)

The compiler plugin looks for every usage of the `create` function from the Ktorfit-lib and adds an object
of the wanted implementation class as an argument.

```kotlin
val api = jvmKtorfit.create<ExampleApi>()
```

will be transformed to:

```kotlin
val api = jvmKtorfit.create<ExampleApi>(_ExampleApiImpl(jvmKtorfit))
```

### Registry Path (enableFactoryRegistry)

When the factory registry is enabled, use `createUsingRegistry` instead — no compiler plugin needed:

```kotlin
@OptIn(ExperimentalFactoryRegistry::class)
val api = jvmKtorfit.createUsingRegistry<ExampleApi>()
```

The `createUsingRegistry()` function looks up the `ClassProvider` from `KtorfitFactoryRegistry`:

```kotlin
public inline fun <reified T : Any> createUsingRegistry(): T {
    val classProvider = KtorfitFactoryRegistry[T::class]
        ?: throw IllegalArgumentException(...)
    return classProvider.create(this)
}
```
