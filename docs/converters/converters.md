Converters are used to convert the HTTPResponse  or parameters.

They are added inside of a Converter.Factory which will then be added to the Ktorfit builder with the **converterfactories()** function.

### Converter Types
* [ResponseConverters](./responseconverter.md)
* [SuspendResponseConverter](./suspendresponseconverter.md)
* [RequestParameterConverter](./requestparameterconverter.md)

### Existing converter factories
* CallConverterFactory
* FlowConverterFactoy