# Migration
Here is how to migrate from deprecated code:

## From <2 to 2.0.0
* You used Response?

Add this dependency:

```kotlin
implementation("de.jensklingenberg.ktorfit:ktorfit-converters-response:$ktorfit")
```

and add this converter to your Ktorfit instance:
```kotlin
.addConverterFactory(ResponseConverterFactory())
```

* You used Call?

Add this dependency:
```kotlin
implementation("de.jensklingenberg.ktorfit:ktorfit-converters-call:$ktorfit")
```

and add this converter to your Ktorfit instance:
```kotlin
.addConverterFactory(CallConverterFactory())
```

* You used Flow?

Add this dependency:
```kotlin
implementation("de.jensklingenberg.ktorfit:ktorfit-converters-flow:$ktorfit")
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