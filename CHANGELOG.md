1.0.0-beta12 (Not released)
========================================
## Breaking Changes: 
wrapResponse from SuspendResponseConverter got renamed to wrapSuspendResponse. This add the possibility to have ResponseConverter and SuspendResponseConverter implemented in the same class.

## Changes: 
- throw compiler time error when you use @Path without the corresponding value inside the relative url path
- every generated implementation class of an interface that Ktorfit generates will now contain a "create" ext function that can be used instead of the generic create() function
e.g. Let's say you have a interface GithubService 

```kotlin
val kttorfit = ktorfit {
baseUrl("http://example.com/")
}.create<GithubService>()
```
or this

```kotlin
val kttorfit = ktorfit {
baseUrl("http://example.com/")
}.createGithubService()
```

By default, IntelliJ/Android Studio can't find the generated code, you need to add the KSP generated folder to the sourcesets 
like this:

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
