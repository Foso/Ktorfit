# Gradle
## Compile errors
By default, Ktorfit will throw compile error when it finds conditions under which it can't ensure that it will work correct.
You can set it in the Ktorfit config to change this

```kotlin
ktorfit{
    errorCheckingMode = ErrorCheckingMode.NONE
}
```

You can set it in your build.gradle.kts file,

* NONE : Turn off all Ktorfit related error checking

* ERROR: Check for errors

* WARNING: Turn errors into warnings

## QualifiedTypeName
By default, Ktorfit will keep qualifiedTypename for TypeData in the generated code empty. You can set it in the Ktorfit config to change this:

```kotlin
ktorfit {
    generateQualifiedTypeName = true
}
```

```kotlin title="Default code generation"
...
val _typeData = TypeData.createTypeData(
    typeInfo = typeInfo<Call<People>>(),
)
...
```

```kotlin title="With QualifiedTypeName true"
...
val _typeData = TypeData.createTypeData(
    typeInfo = typeInfo<Call<People>>(),
    qualifiedTypename = "de.jensklingenberg.ktorfit.Call<com.example.model.People>"
)
...
```

## Per-Target Generation

By default, Ktorfit uses `kspCommonMainKotlinMetadata` to generate shared code for all targets in a Kotlin Multiplatform project. When you enable `perTargetGeneration`, each target runs KSP independently and generates its own copy of the implementation code. This removes the dependency on the metadata task chain.

```kotlin
ktorfit {
    perTargetGeneration = true
}
```

### When to use it

Enable this option when you need to eliminate the `kspCommonMainKotlinMetadata` dependency from your module's build. With per-target generation:

- Each KMP target processes sources independently
- Generated `_<Name>Impl.kt` files self-register into `KtorfitApiRegistry`
- You can use `ktorfit.createApi<T>()` from `commonMain` without a compiler plugin
- Multi-module projects avoid file-name collisions since each Impl file registers itself individually

### createApi alternative

When `perTargetGeneration` is enabled, each generated file registers itself automatically. You can then use the `createApi<T>()` method instead of the generated extension function:

```kotlin
// Extension function (always available)
val api = ktorfit.createExampleApi()

// Registry-based lookup (requires perTargetGeneration = true)
val api: ExampleApi = ktorfit.createApi<ExampleApi>()
```

# Ktorfit Builder

## Add your own Ktor client
You can set your Ktor client instance to the Ktorfit builder:

```kotlin
val myClient = HttpClient()
val ktorfit = Ktorfit.Builder().httpClient(myClient).build()
```

