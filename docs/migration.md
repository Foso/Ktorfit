# Migration
Here is how to migrate from deprecated code:

## From <2 to 2.0.0

* Versioning

Starting with 2.0.0 the Ktorfit dependencies will not have exactly the same version numbers.

The Ktorfit library (de.jensklingenberg.ktorfit:ktorfit-lib/de.jensklingenberg.ktorfit:ktorfit-lib-light) will have the version number of the Ktorfit version e.g. 2.0.0.

The KSP plugin (de.jensklingenberg.ktorfit:ktorfit-ksp) will have KTORFIT_VERSION-KSP_VERSION as version number, e.g. 2.0.0-1.0.20

The Gradle plugin (de.jensklingenberg.ktorfit) will have KTORFIT_VERSION-KOTLIN_VERSION as version number, e.g. 2.0.0-2.0.0

* You used Response?

Add this dependency:

You can find all available versions [here](https://repo.maven.apache.org/maven2/de/jensklingenberg/ktorfit/ktorfit-converters-response/)

```kotlin
implementation("de.jensklingenberg.ktorfit:ktorfit-converters-response:$CONVERTER_VERSION")
```

and add this converter to your Ktorfit instance:
```kotlin
.addConverterFactory(ResponseConverterFactory())
```

* You used Call?

Add this dependency:

You can find all available versions [here](https://repo.maven.apache.org/maven2/de/jensklingenberg/ktorfit/ktorfit-converters-call/)

```kotlin
implementation("de.jensklingenberg.ktorfit:ktorfit-converters-call:$CONVERTER_VERSION")
```

and add this converter to your Ktorfit instance:
```kotlin
.addConverterFactory(CallConverterFactory())
```

* You used Flow?

Add this dependency:

You can find all available versions [here](https://repo.maven.apache.org/maven2/de/jensklingenberg/ktorfit/ktorfit-converters-flow/)

```kotlin
implementation("de.jensklingenberg.ktorfit:ktorfit-converters-flow:$CONVERTER_VERSION")
```

and add this converter to your Ktorfit instance:
```kotlin
.addConverterFactory(FlowConverterFactory())
```

## From 1.7.0 to 1.8.1

### SuspendResponseConverter
Implement **override suspend fun convert(result: KtorfitResult)**

```kotlin
public suspend fun convert(result: KtorfitResult): T {
    return when (result) {
        is KtorfitResult.Failure -> {
            throw result.throwable // Or do something with the throwable
        }

        is KtorfitResult.Success -> {
            val response = result.response
            //Put the code that was in your other convert function here
        }
    }
}
```

Redirect the deprecated function to the new function:

```kotlin
override suspend fun convert(response: HttpResponse): Any {
    return convert(KtorfitResult.Success(response))
}
```