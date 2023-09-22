# Changelog

All important changes of this project must be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

1.7.0 - 2023-09-16
========================================
# Gradle plugin
From now on with every Ktorfit release there will also be a Gradle plugin with the same version.
That means that drop you can drop the Gradle extension block where you previously set the version number and just bump the number of the Gradle plugin. https://foso.github.io/Ktorfit/installation/

```kotlin
plugins {
id("de.jensklingenberg.ktorfit") version "1.7.0"
}
```

### Added
- Added a compiler type checks if the type used for the create function is an interface

### Changed
- Upgrade dependencies: Ktor 2.3.4

### Deprecated
### Removed
### Fixed
### Security

1.6.0 - 2023-08-24
========================================

### Added
### Changed
* KSP version 1.9.10-1.0.13 is now required
- Upgrade dependencies: Ktor 2.3.3

### Deprecated
### Removed
### Fixed
### Security

1.5.0 - 2023-08-04
========================================

### Added
### Changed
* KSP version 1.9.0-1.0.13 is now needed

### Deprecated
### Removed
### Fixed
### Security

1.4.4 - 2023-07-26
========================================

### Added
### Changed
- Upgrade dependencies: Ktor 2.3.2

### Deprecated
### Removed
### Fixed
### Security

1.4.3 - 2023-07-13
========================================

### Added
### Changed
### Deprecated
### Removed
### Fixed
#372 Crash with Xiaomi on create Ktorfit.Builder by @princeparadoxes

### Security

1.4.2 - 2023-06-25
========================================

### Added
### Changed
### Deprecated
### Removed
### Fixed
#323 Code generation issue for @Multipart / @FormUrlEncoded by @Ph1ll1pp

### Security

1.4.1 - 2023-06-03
========================================

### Added

### Changed
- Upgrade dependencies: Ktor 2.3.1

### Deprecated

### Removed

### Fixed
#236 Parsing error for list/array

### Security

1.4.0 - 2023-05-27
========================================

### Added
* #85 Added a Response class that can be used as a wrapper around the API Response, the converter for it is automatically applied. thx to @vovahost, @DATL4G

e.g.

```kotlin title=""
interface ExampleApi{
  suspend fun getUser(): Response<User>
}

val user = userKtorfit.create<ExampleApi>().getUser()
    
if(user.isSuccessful){
  user.body()
}else{
  user.errorBody()
}
```

* Ktorfit is now using converters factories to apply the converters, similar to Retrofit 
 see more here https://foso.github.io/Ktorfit/converters/converters/
* TypeData now has a field "typeInfo" can be used to convert the Ktor HttpResponse body to the wanted type
* CallConverterFactory for replacement of CallResponseConverter
* FlowConverterFactory for replacement of FlowResponseConverter

* Added support for targets:
macosArm64, tvosArm64, tvosX64, tvosSimulatorArm64, watchosSimulatorArm64 #315

### Changed
- Upgrade dependencies: Kotlin 1.8.21

### Deprecated
* ResponseConverter, use Converter.ResponseConverter instead
* SuspendResponseConverter, use Converter.SuspendResponseConverter instead
* RequestConverter, use Converter.RequestParameterConverter instead
* See also: https://foso.github.io/Ktorfit/converters/migration/

### Removed

### Fixed

### Security

1.3.0 - 2023-05-14
========================================

### Added

### Changed
* Optimized generated code, the generated code that is used for a request will 
 now directly set the Ktor code instead of delegating it to a Ktorfit class. This will
 make the code easier to understand.

* KSP version 1.0.11 is now needed

### Deprecated

### Removed

### Fixed
[Bug]: IllegalArgumentException with Custom Http Annotation #274

### Security

### Bumped
KSP version to 1.0.11

1.2.0 - 2023-05-05
========================================

### Added

### Changed

### Deprecated

### Removed

### Fixed

### Security

### Bumped
Now based on Ktor 2.3.0

1.1.0 - 2023-04-15
========================================

### Added
From now on there are two versions of the ktorfit-lib.

"de.jensklingenberg.ktorfit:ktorfit-lib"
will stay like before and add the platform client dependencies for the clients.

"de.jensklingenberg.ktorfit:ktorfit-lib-light"
this will only add the client core dependency and not the platform dependencies for the clients.
This will give you more control over the used clients, but you have to add them yourself. https://ktor.io/docs/http-client-engines.html
Everything else is the same as "ktorfit-lib"

### Changed
* Kotlin version 1.8.20 is now needed
* KSP version 1.8.20-1.0.10 is now needed

### Deprecated

### Removed

### Fixed

### Security

### Bumped
* Kotlin to 1.8.20
* KSP version to 1.8.20-1.0.10

1.0.1 - 2023-03-20
========================================

### Added
From now on there are two versions of the ktorfit-lib. 

"de.jensklingenberg.ktorfit:ktorfit-lib"
will stay like before and add the platform client dependencies for the clients.

"de.jensklingenberg.ktorfit:ktorfit-lib-light"
this will only add the client core dependency and not the platform dependencies for the clients.
This will give you more control over the used clients, but you have to add them yourself. https://ktor.io/docs/http-client-engines.html
Everything else is the same as "ktorfit-lib"

### Changed

### Deprecated

### Removed

### Fixed
[Bug]: Post request body serialization doesn't work #202

### Security

### Bumped

---

1.0.0 - 2023-03-02
========================================
This project is now following [semver](https://semver.org/)

### Added
* internal optimizations
* throw compile error when generated class can not be found

### Fixed

* Timeout throws exception outside of scope of SuspendResponseConverter #127
* Fix broken/outdated docs link (#140) by @T-Spoon

### Bumped

- based on Ktor 2.2.4

1.0.0-beta18 (12-02-2023)
========================================
NEW:

* You can now disable the check if the baseUrl ends with a /

```kotlin
Ktorfit.Builder().baseUrl(testBaseUrl, checkUrl = false).build()
```

🐛 Bugs fixed

* Fixed Ktorfit breaking incremental compilation #110

⬆️ Deps updates

- based on Ktor 2.2.3

1.0.0-beta17 (21-01-2023)
========================================
💥 Breaking changes:

- Ktorfit now needs an additional gradle plugin. This will solve several issues with multi-module projects.

Add this [plugin](https://plugins.gradle.org/plugin/de.jensklingenberg.ktorfit):
```kotlin
plugins {
  id "de.jensklingenberg.ktorfit" version "1.0.0"
}
```

NEW:

* interfaces can now be internal

🐛 Bugs fixed

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
