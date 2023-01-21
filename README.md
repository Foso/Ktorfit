<h1>Ktorfit</h1>

[![All Contribtors](https://img.shields.io/badge/Maven-Central-download.svg?style=flat-square)](https://mvnrepository.com/artifact/de.jensklingenberg.ktorfit) [![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://github.com/Foso/Ktorfit)
[![jCenter](https://img.shields.io/badge/Apache-2.0-green.svg)](https://github.com/Foso/Ktorfit/blob/master/LICENSE)
[Documentation](http://foso.github.io/Ktorfit)

![badge-android](http://img.shields.io/badge/platform-android-6EDB8D.svg?style=flat)
![badge-ios](http://img.shields.io/badge/platform-ios-CDCDCD.svg?style=flat)
![badge-mac](http://img.shields.io/badge/platform-macos-111111.svg?style=flat)
![badge-watchos](http://img.shields.io/badge/platform-watchos-C0C0C0.svg?style=flat)
![badge-jvm](http://img.shields.io/badge/platform-jvm-DB413D.svg?style=flat)
![badge-linux](http://img.shields.io/badge/platform-linux-2D3F6C.svg?style=flat)
![badge-windows](http://img.shields.io/badge/platform-windows-4D76CD.svg?style=flat)
![badge-js](http://img.shields.io/badge/platform-js-F8DB5D.svg?style=flat)
![badge-nodejs](https://img.shields.io/badge/platform-nodejs-F8DB5D.svg?style=flat)
<p align="center">
  <img src ="https://raw.githubusercontent.com/Foso/Experimental/master/carbon.png"  />
</p>

## Introduction

Ktorfit is a HTTP client/Kotlin Symbol Processor for Kotlin Multiplatform ( Android, iOS, Js, Jvm,  Linux) using [KSP](https://github.com/google/ksp) and [Ktor clients](https://ktor.io/docs/getting-started-ktor-client.html) inspired by [Retrofit](https://square.github.io/retrofit/)

## Show some :heart: and star the repo to support the project

[![GitHub stars](https://img.shields.io/github/stars/Foso/Ktorfit.svg?style=social&label=Star)](https://github.com/Foso/Ktorfit) [![GitHub forks](https://img.shields.io/github/forks/Foso/Ktorfit.svg?style=social&label=Fork)](https://github.com/Foso/Ktorfit/fork) [![GitHub watchers](https://img.shields.io/github/watchers/Foso/Ktorfit.svg?style=social&label=Watch)](https://github.com/Foso/Ktorfit) [![Twitter Follow](https://img.shields.io/twitter/follow/jklingenberg_.svg?style=social)](https://twitter.com/jklingenberg_)

## How to use
For more documentation check: [http://foso.github.io/Ktorfit](http://foso.github.io/Ktorfit)

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
Because Ktor relies on Coroutines by default your functions need to have the **suspend** modifier. Alternatively you can use [#Flow](https://foso.github.io/Ktorfit/#flow) or [Call](https://foso.github.io/Ktorfit/#call) 


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
When you want to use Ktor plugins for things like serialization, you need to add the dependencies and they need to be compatible with 2.1.0

## 📙 Documentation
In this Readme is only a basic example, for more documentation check: [http://foso.github.io/Ktorfit](http://foso.github.io/Ktorfit)


## 👷 Project Structure
* <kbd>ktorfit-annotations</kbd> - module with annotations for the Ktorfit
* <kbd>ktorfit-ksp</kbd> - module with source for the KSP plugin
* <kbd>ktorfit-lib</kbd> - module with source for the Ktorfit lib
* <kbd>sandbox</kbd> - experimental test module to try various stuff

* <kbd>example</kbd> - contains example projects that use Ktorfit
* <kbd>docs</kbd> - contains the source for the GitHub page

## ✍️ Feedback

Feel free to send feedback on [Twitter](https://twitter.com/jklingenberg_) or [file an issue](https://github.com/foso/Ktorfit/issues/new). Feature requests/Pull Requests are always welcome. 


## Credits

Ktorfit is brought to you by these [contributors](https://github.com/Foso/Ktorfit/graphs/contributors).


## 📜 License

This project is licensed under the Apache License, Version 2.0 - see the [LICENSE.md](https://github.com/Foso/Ktorfit/blob/master/LICENSE) file for details

