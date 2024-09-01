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

# Ktorfit Builder

## Add your own Ktor client
You can set your Ktor client instance to the Ktorfit builder:

```kotlin
val myClient = HttpClient()
val ktorfit = Ktorfit.Builder().httpClient(myClient).build()
```

