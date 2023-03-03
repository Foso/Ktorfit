# Scope of Ktorfit

The goal of Ktorfit is to provide a similar developer experience like [Retrofit](https://square.github.io/retrofit/) for Kotlin Multiplatform projects. It uses [Ktor clients](https://ktor.io/docs/getting-started-ktor-client.html) because they are available on nearly every compile target of KMP.
Every feature should be implemented so that it works on all platforms that Ktor supports. Before a new functionality is added to Ktorfit, it should be checked if there is already a Ktor plugin for it which solves the same problem.