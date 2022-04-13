package de.jensklingenberg.ktorfit.model.annotations

/**
 * Annotation at a parameter
 */
open class ParamAnnotation
class Body : ParamAnnotation()
class RequestBuilder : ParamAnnotation()
class Path(val value: String, val encoded: Boolean = false) : ParamAnnotation()
class Query(val value: String, val encoded: Boolean = false) : ParamAnnotation()
class QueryName(val encoded: Boolean) : ParamAnnotation()
class QueryMap(val encoded: Boolean) : ParamAnnotation()
class Header(val path: String) : ParamAnnotation()
class HeaderMap : ParamAnnotation()
class Url : ParamAnnotation()
class Field(val value: String, val encoded: Boolean = false) : ParamAnnotation()
class FieldMap(val encoded: Boolean) : ParamAnnotation()
class Part(val value: String = "", val encoding: String = "binary") : ParamAnnotation()
class PartMap(val encoding: String = "binary") : ParamAnnotation()