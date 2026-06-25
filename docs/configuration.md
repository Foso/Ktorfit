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
- KSP generates `actual` implementations for each platform target
- You can use the generated `createXxx()` extension function from `commonMain`

### expect/actual pattern

When `perTargetGeneration = true`, each API interface in `commonMain` requires an `expect` declaration. KSP validates this at compile time and generates the `actual` implementation in each platform target.

```kotlin
// commonMain — user writes both:
interface ExampleApi {
    @GET("users")
    suspend fun getUsers(): List<User>
}

expect fun Ktorfit.createExampleApi(): ExampleApi

// jvmMain, jsMain, nativeMain, etc. — KSP generates:
actual fun Ktorfit.createExampleApi(): ExampleApi =
    _ExampleApiImpl(this.baseUrl, this.httpClient, KtorfitConverterHelper(this))
```

**Usage from commonMain:**

```kotlin
val api = ktorfit.createExampleApi()
```

If the `expect` declaration is missing, KSP emits a compile error telling you exactly what to add. This is the same pattern used by Room for Kotlin Multiplatform.

# Ktorfit Builder

## Add your own Ktor client
You can set your Ktor client instance to the Ktorfit builder:

```kotlin
val myClient = HttpClient()
val ktorfit = Ktorfit.Builder().httpClient(myClient).build()
```

