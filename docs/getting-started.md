#  Getting started


## Setup
(You can also look how it's done in the [examples](https://github.com/Foso/Ktorfit/tree/master/example))

#### Gradle Plugins
You need to add KSP and the [Ktorfit Gradle plugin](https://plugins.gradle.org/plugin/de.jensklingenberg.ktorfit)
```kotlin

plugins {
  id("com.google.devtools.ksp") version "1.7.20-1.0.8"
  id("de.jensklingenberg.ktorfit") version "1.0.0"

}
```

Next you have to add the Ktorfit KSP Plugin to the common target and every compilation target, where you want to use Ktorfit.


```kotlin
val ktorfitVersion = "LATEST_KTORFIT_VERSION"

dependencies {
    add("kspCommonMainMetadata", "de.jensklingenberg.ktorfit:ktorfit-ksp:$ktorfitVersion")
    add("ksp[NAMEOFPLATFORM]","de.jensklingenberg.ktorfit:ktorfit-ksp:$ktorfitVersion")
        ...
}
```

[NAMEOFPLATFORM] is the name of the compilation target. When you want to use it for the Android module it's **kspAndroid**, for Js it's **kspJs**, etc.
Look here for more information https://kotlinlang.org/docs/ksp-multiplatform.html


#### Ktorfit-lib

Add the Ktorfit-lib to your common module.
```kotlin
val ktorfitVersion = "LATEST_KTORFIT_VERSION"

sourceSets {
    val commonMain by getting{
        dependencies{
            implementation("de.jensklingenberg.ktorfit:ktorfit-lib:$ktorfitVersion")
        }
    }
```

#### Ktor
Ktorfit is based on Ktor Clients 2.2.2. You don't need to add an extra dependency for the default clients.
When you want to use Ktor plugins for things like serialization, you need to add the dependencies and they need to be compatible with 2.0.1


## How to use
First do the [Setup](#setup)

Let's say you want to make a GET Request to https://swapi.dev/api/people/1/

Create a new Kotlin interface

```kotlin
interface ExampleApi {
    @GET("people/1/")
    suspend fun getPerson(): String
}
```

!!! info

    The return type String will return the response text. When you want directly parse the response into a class you need to add a JSON,XML, etc. converter to Ktor

Now we add a function that will be used to make our request. The @GET annotation will tell Ktorfit that this a GET request. The value of @GET is the relative URL path that will be appended to the base url which we set later.

An interface used for Ktorfit needs to have a Http method annotation on every function.
Because Ktor relies on Coroutines by default your functions need to have the **suspend** modifier. Alternatively you can use [#Flow](./suspendresponseconverter.md#flow) or [Call](./suspendresponseconverter.md#call)


```kotlin
val ktorfit = Ktorfit.Builder().baseUrl("https://swapi.dev/api/").build()
val exampleApi = ktorfit.create<ExampleApi>()
```

Next we need to create a Ktorfit object, in the constructor we set the base url.
We can then use the **create()** function to receive an implementation of the wanted type.

```kotlin
val response = exampleApi.getPerson()
println(response)
```

Now we can use exampleApi to make the request.