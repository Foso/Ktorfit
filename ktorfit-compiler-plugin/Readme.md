# Compiler plugin
The compiler plugin transform the usage of the create function from Ktorfit-lib

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

# Compatibility table
| Compiler plugin version | Kotlin    |
|-------------------------|-----------|
| 2.3.2                   | 2.2.21    |
| 2.3.3                   | 2.3.0-RC3 |

