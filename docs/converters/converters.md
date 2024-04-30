Converters are used to convert the HTTPResponse  or parameters.

They are added inside of a Converter.Factory which will then be added to the Ktorfit builder with the **converterfactories()** function.

### Converter Types
* [ResponseConverters](./responseconverter.md)
* [SuspendResponseConverter](./suspendresponseconverter.md)
* [RequestParameterConverter](./requestparameterconverter.md)

### Existing converter factories
* CallConverterFactory
  
Add this dependency:
```kotlin
implementation("de.jensklingenberg.ktorfit:ktorfit-converters-call:$ktorfit")
```

* FlowConverterFactory

Add this dependency:
```kotlin
implementation("de.jensklingenberg.ktorfit:ktorfit-converters-flow:$ktorfit")
```

* ResponseConverterFactory

Add this dependency:
```kotlin
implementation("de.jensklingenberg.ktorfit:ktorfit-converters-response:$ktorfit")
```