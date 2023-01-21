1.0.0-beta17 (not released)
========================================
## BREAKING CHANGE:
- Ktorfit now needs an additional gradle plugin. This will solve serveral issues with multi-module projects.

Add this [plugin](https://plugins.gradle.org/plugin/de.jensklingenberg.ktorfit):
```kotlin
plugins {
  id "de.jensklingenberg.ktorfit" version "1.0.0"
}
```

## NEW

- interfaces can now be internal

## FIXED
* Ktorfit multiple module support #92
* Add support for 'internal' parameter type #13
* Duplicate class KtorfitExtKt found in modules moduleA and moduleB #86
*  Android overload resolution ambiguity #64
*  Form data is double encoded #95

⬆️ Deps updates
- based on Ktor 2.2.2
- Kotlin 1.8.0
- KSP 1.8.0-1.0.8
- update Android TargetSdk to 33


1.0.0-beta16 (13-11-2022)
========================================

NEW:
- Field parameters can now be nullable, null values will be ignored in requests
- Add option to turn of error checking

  **ksp {
  arg("Ktorfit_Errors", "1")
  }**
  
  You can set it in your build.gradle.kts file, 
  
  0: Turn off all Ktorfit related error checking
  
  1: Check for errors
  
  2: Turn errors into warnings
  
-  Added RequestConverter support #84

⬆️ Deps updates
- based on Ktor 2.1.3
- Kotlin 1.7.21
- KSP 1.0.8
- update Android TargetSdk to 33

🐛 Bugs fixed
- FlowResponseConverter #81

## What's Changed
* build(deps): bump logback-classic from 1.4.0 to 1.4.3 by @dependabot in https://github.com/Foso/Ktorfit/pull/74
* Foso/revert converters changes by @Foso in https://github.com/Foso/Ktorfit/pull/76
* 67 add nullable field parameters support by @Foso in https://github.com/Foso/Ktorfit/pull/80
* fix: FlowResponseConverter by @Foso in https://github.com/Foso/Ktorfit/pull/81
* Added RequestConverter support by @DATL4G in https://github.com/Foso/Ktorfit/pull/84
* feat: add option to turn off error checking #77 by @Foso in https://github.com/Foso/Ktorfit/pull/88


**Full Changelog**: https://github.com/Foso/Ktorfit/compare/v1.0.0-beta15...v1.0.0-beta16

1.0.0-beta15 (05-10-2022)
========================================
⬆️ Deps updates
- based on Ktor 2.1.2

🐛 Bugs fixed
- kotlinx.coroutines.JobCancellationException: Parent job is Completed #70

💥 Breaking changes
- reverted the api of converters to the state of beta13, see #71 
- when you are updating from beta13, this is the only change to converters:
  returnTypeName is replaced through typeData, you can use typeData.qualifiedName to get the same value as returnTypeName


1.0.0-beta14 (24-09-2022)
========================================
NEW:
- Query parameters can now be nullable, null values will be ignored in requests
- Function return types can now be nullable

FIX:
- Url annotation not resolved correctly #65

BREAKING CHANGES:
- Changed naming of Converters:

  - SuspendResponseConverter:
    - is now called RequestConverter
    - the wrapSuspendResponse is now called convertRequest.
    - returnTypeName is replaced through typeData, you can use typeData.qualifiedName to get the same value as returnTypeName
      RequestConverter need to be added with the requestConverter() on your Ktorfit object.
    - [https://foso.github.io/Ktorfit/requestconverter/](https://foso.github.io/Ktorfit/requestconverter/)
  - ResponseConverters:
    - returnTypeName is replaced through typeData, you can use typeData.qualifiedName to get the same value as returnTypeName
      [https://foso.github.io/Ktorfit/responseconverter/](https://foso.github.io/Ktorfit/responseconverter/)


1.0.0-beta13 (10-09-2022)
========================================
- KtorfitCallResponseConverter and KtorfitSuspendCallResponseConverter are now combined in KtorfitCallResponseConverter
- based on Ktor 2.1.1

Fixed:
- Url annotation not resolved correctly #52

1.0.0-beta12 (31-08-2022)
========================================
## Breaking Changes: 
wrapResponse from SuspendResponseConverter got renamed to wrapSuspendResponse. This add the possibility to have ResponseConverter and SuspendResponseConverter implemented in the same class.

## Changes: 
- throw compiler time error when you use @Path without the corresponding value inside the relative url path
- every generated implementation class of an interface that Ktorfit generates will now contain a "create" ext function that can be used instead of the generic create() function
e.g. Let's say you have a interface GithubService, then you can create an instance like this:

```kotlin
val ktorfit = ktorfit {
baseUrl("http://example.com/")
}.create<GithubService>()
```
or this

```kotlin
val ktorfit = ktorfit {
baseUrl("http://example.com/")
}.createGithubService()
```

By default, IntelliJ/Android Studio can't find the generated code, you need to add the KSP generated folder to the sourcesets 
like this: (See more here: https://kotlinlang.org/docs/ksp-quickstart.html#make-ide-aware-of-generated-code)

```kotlin
kotlin.srcDir("build/generated/ksp/jvm/jvmMain/")
```


1.0.0-beta11 (21-08-2022)
========================================

- you can now use ResponseConverter in combination with suspend functions. Implement the SuspendResponseConverter
- KtorfitCallResponseConverter and FlowResponseConverter moved to de.jensklingenberg.ktorfit.converter.builtin


1.0.0-beta10 (18-08-2022)
========================================

- based on Ktor 2.0.2
- added windows target #26
- @PATCH, @POST, @PUT now have a default value #22
- Ktorfit now uses a builder pattern for setup
 e.g. change this: 
 ```kotlin
 Ktorfit("https://example.com/", HttpClient {})
```
 

to this: 

```kotlin
Ktorfit.Builder()
.baseUrl("https://example.com/")
.httpClient(HttpClient {})
.build()
```

- 
## Breaking Changes:
@Headers now requires a vararg of String instead of an Array
e.g. you need to change from:

```kotlin
@Headers(
["Authorization: token ghp_abcdefgh",
"Content-Type: application/json"]
)
```


to this:
```kotlin
@Headers(
"Authorization: token ghp_abcdefgh",
"Content-Type: application/json"
)
```


1.0.0-beta09
========================================
- #15 fix encoding of query parameters

1.0.0-beta08
========================================
- fix issue with Koin Annotations


1.0.0-beta07
========================================
- fix issue with FormUrlEncoded
- based on Ktor 2.0.2

1.0.0-beta06
========================================
- fix issue with KSP 1.0.5 #19

1.0.0-beta05
========================================
- fixed: Custom Http Method with @Body is now possible #6
- based on Ktor 2.0.1
- cleanup example project @mattrob33

1.0.0-beta04
========================================
initial release
