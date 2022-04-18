package de.jensklingenberg.ktorfit.model.annotations

/**
 * Annotation at a parameter
 */
open class ParameterAnnotation
class Body : ParameterAnnotation()
class RequestBuilder : ParameterAnnotation()
class Path(val value: String, val encoded: Boolean = false) : ParameterAnnotation()
class Query(val value: String, val encoded: Boolean = false) : ParameterAnnotation()
class QueryName(val encoded: Boolean= false) : ParameterAnnotation()
class QueryMap(val encoded: Boolean= false) : ParameterAnnotation()
class Header(val path: String) : ParameterAnnotation()
class HeaderMap : ParameterAnnotation()
class Url : ParameterAnnotation()
class Field(val value: String, val encoded: Boolean = false) : ParameterAnnotation()
class FieldMap(val encoded: Boolean) : ParameterAnnotation()
class Part(val value: String = "", val encoding: String = "binary") : ParameterAnnotation()
class PartMap(val encoding: String = "binary") : ParameterAnnotation()