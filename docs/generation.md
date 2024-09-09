### `@NoDelegation`

The `@NoDelegation` annotation is used in Kotlin to indicate that a specific interface should not be implemented using Kotlin delegation. When an interface is annotated with `@NoDelegation`, the generated implementation class will not delegate the implementation of that interface to another class.

#### Usage

To use the `@NoDelegation` annotation, simply annotate the interface that you do not want to be delegated. This is particularly useful in scenarios where you want to ensure that the methods of the interface are implemented directly in the class rather than being delegated to another implementation.

#### Example

Below is an example demonstrating the use of the `@NoDelegation` annotation:

```kotlin
package com.example.api

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.NoDelegation

interface SuperTestService1 {
    @GET("posts")
    suspend fun test(): String
}

interface SuperTestService2 {
    suspend fun test(): String
}

// TestService extends interfaces with and without @NoDelegation
interface TestService : SuperTestService1, @NoDelegation SuperTestService2 {
    @GET("posts")
    override suspend fun test(): String

    @GET("posts")
    suspend fun test(): String
}
```

In this example:
- `SuperTestService1` is a regular interface without the `@NoDelegation` annotation.
- `SuperTestService2` is a interface annotated with `@NoDelegation`.

When generating the implementation class for `TestService`, the methods from `SuperTestService2` will not be delegated to another implementation.
Please be aware that Ktorfit only generates code for the functions that are annotated inside a interface.
When you extend a interface and you use @NoDelegation, you have to make sure that every function from extended interfaces are
overridden in the interface that you want to use with Ktorfit.
Otherwise you will get compile error because the function is missing.

#### Generated Implementation

The generated implementation class for `TestService` will look like this:

```kotlin
public class _TestServiceImpl(
    private val _ktorfit: Ktorfit,
) : TestService, SuperTestService1 by com.example.api._SuperTestService1Impl(_ktorfit) {
    // No delegation for SuperTestService1 and SuperTestService2
}
```