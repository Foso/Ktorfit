1.0.0-beta10
========================================
(18.08.2022)

- based on Ktor 2.0.2
- added windows target #26
- @PATCH, @POST, @PUT now have a default value #22
- Ktorfit now uses a builder pattern for setup
 e.g. change this: 
  Ktorfit("https://example.com/", HttpClient {})

to this: 

Ktorfit.Builder()
.baseUrl("https://example.com/")
.httpClient(HttpClient {})
.build()
- 
**Breaking Changes: **
@Headers now requires a vararg of String instead of an Array
e.g. you need to change from:

@Headers(
["Authorization: token ghp_abcdefgh",
"Content-Type: application/json"]
)

to this:

@Headers(
"Authorization: token ghp_abcdefgh",
"Content-Type: application/json"
)

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
