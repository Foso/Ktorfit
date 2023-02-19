# Compile errors
By default, Ktorfit will throw compile error when it finds conditions under which it can't ensure that it will work correct.
You can set an KSP argument to change this:

```kotlin
ksp {
    arg("Ktorfit_Errors", "1")
}
```

You can set it in your build.gradle.kts file,

0: Turn off all Ktorfit related error checking

1: Check for errors

2: Turn errors into warnings
