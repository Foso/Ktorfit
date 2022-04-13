<h1>Ktorfit</h1>

[![All Contribtors](https://img.shields.io/badge/Maven-Central-download.svg?style=flat-square)](https://mvnrepository.com/artifact/de.jensklingenberg.ktorfit)
[![jCenter](https://img.shields.io/badge/Apache-2.0-green.svg)](https://github.com/Foso/Ktorfit/blob/master/LICENSE) [![GitHub release (latest SemVer)](https://img.shields.io/github/v/release/foso/ktorfit)](https://github.com/foso/ktorfit/releases)

<p align="center">
  <img src ="https://raw.githubusercontent.com/Foso/Experimental/master/carbon.png"  />
</p>

## Introduction

Ktorfit is a HTTP client/Kotlin Symbol Processor for Kotlin Multiplatform (Js, Jvm, Android, iOS, Linux) using [KSP](https://github.com/google/ksp) and [Ktor clients](https://ktor.io/docs/getting-started-ktor-client.html) inspired by [Retrofit](https://square.github.io/retrofit/)

## Show some :heart: and star the repo to support the project

[![GitHub stars](https://img.shields.io/github/stars/Foso/Ktorfit.svg?style=social&label=Star)](https://github.com/Foso/Ktorfit) [![GitHub forks](https://img.shields.io/github/forks/Foso/Ktorfit.svg?style=social&label=Fork)](https://github.com/Foso/Ktorfit/fork) [![GitHub watchers](https://img.shields.io/github/watchers/Foso/Ktorfit.svg?style=social&label=Watch)](https://github.com/Foso/Ktorfit) [![Twitter Follow](https://img.shields.io/twitter/follow/jklingenberg_.svg?style=social)](https://twitter.com/jklingenberg_)


## How to use
For more documentation check: http://foso.github.io/Ktorfit

First do the [Setup](#setup)

Let's say you want to make a GET Request to https://swapi.dev/api/people/1/

Create a new Kotlin interface

```kotlin
interface ExampleApi {
    @GET("people/1/")
    suspend fun getPerson(): String
}
```

Now we add a function that will be used to make our request. The @GET annotation will tell Ktorfit that this a GET request. The value of @GET is the relative URL path that will be appended to the base url which we set later.

An interface used for Ktorfit needs to have a Http method annotation on every function.
Because Ktor relies on Coroutines by default your functions need to have the **suspend** modifier. Alternatively you can use [#Flow](#flow) or Call (see below)


```kotlin
val ktorfit = Ktorfit(baseUrl = "https://swapi.dev/api/")
val exampleApi = ktorfit.create<ExampleApi>()
```

Next we need to create a Ktorfit object, in the constructor we set the base url.
We can then use the **create()** function to receive an implementation of the wanted type.

```kotlin
val response = exampleApi.getPerson()
println(response)
```

Now we can use exampleApi to make the request.



## Setup
(You can also look how it's done in the [examples](https://github.com/Foso/Ktorfit/tree/master/example))
For Kotlin Native Targets (iOS,Linux) you need to enable the new memory model in gradle.properties

```kotlin
kotlin.native.binary.memoryModel=experimental
```

#### KSP
When you are not using KSP already you need to apply the plugin in your build.gradle
```kotlin

plugins {
  id("com.google.devtools.ksp") version "1.6.20-1.0.4"
}
```

Next you have to add the Ktorfit KSP Plugin to the common target and every compilation target, where you want to use Ktorfit.


```kotlin
val ktorfitVersion = "1.0.0-beta04"

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
val ktorfitVersion = "1.0.0-beta04"

sourceSets {
    val commonMain by getting{
        dependencies{
            implementation("de.jensklingenberg.ktorfit:ktorfit-lib:$ktorfitVersion")
        }
    }
```

#### Ktor
Ktorfit is based on Ktor Clients 2.0.0. You don't need to add an extra dependency for the default clients.
When you want to use Ktor plugins for things like serialization, you need to add the dependencies and they need to be compatible with 2.0.0


## 👷 Project Structure
 	
* <kbd>ktorfit-ksp</kbd> - module with source for the KSP plugin
* <kbd>ktorfit-lib</kbd> - module with source for the Ktorfit lib
* <kbd>workload</kbd> - experimental test module to test various stuff

* <kbd>example</kbd> - contains example projects that use Ktorfit
* <kbd>docs</kbd> - contains the source for the github page

## ✍️ Feedback

Feel free to send feedback on [Twitter](https://twitter.com/jklingenberg_) or [file an issue](https://github.com/foso/Ktorfit/issues/new). Feature requests are always welcome. 


## 📜 License

This project is licensed under the Apache License, Version 2.0 - see the [LICENSE.md](https://github.com/Foso/Ktorfit/blob/master/LICENSE) file for details
