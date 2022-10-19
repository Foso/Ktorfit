package de.jensklingenberg.ktorfit.model

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSNode

class KtorfitError {

    companion object {
        const val FUNCTION_OR_PARAMETERS_TYPES_MUST_NOT_INCLUDE_ATYPE_VARIABLE_OR_WILDCARD =
            "function or parameters types must not include a type variable or wildcard:"
        const val FORM_ENCODED_METHOD_MUST_CONTAIN_AT_LEAST_ONE_FIELD_OR_FIELD_MAP =
            "Form-encoded method must contain at least one @Field or @FieldMap."
        const val HEADER_MAP_PARAMETER_TYPE_MUST_BE_MAP = "@HeaderMap parameter type must be Map."
        const val HEADER_MAP_KEYS_MUST_BE_OF_TYPE_STRING = "@HeaderMap keys must be of type String:"
        const val QUERY_MAP_PARAMETER_TYPE_MUST_BE_MAP = "@QueryMap parameter type must be Map."
        const val QUERY_MAP_KEYS_MUST_BE_OF_TYPE_STRING = "@QueryMap keys must be of type String:"
        const val URL_PARAMETER_TYPE_MAY_NOT_BE_NULLABLE = "Url parameter type may not be nullable"

        const val FIELD_MAP_PARAMETER_TYPE_MUST_BE_MAP = "@FieldMap parameter type must be Map."
        const val FIELD_MAP_KEYS_MUST_BE_OF_TYPE_STRING = "@FieldMap keys must be of type String:"
        const val ONLY_ONE_REQUEST_BUILDER_IS_ALLOWED = "Only one RequestBuilder is allowed."
        const val REQ_BUILDER_PARAMETER_TYPE_NEEDS_TO_BE_HTTP_REQUEST_BUILDER =
            "@ReqBuilder parameter type needs to be HttpRequestBuilder.()->Unit"
        const val FIELD_PARAMETERS_CAN_ONLY_BE_USED_WITH_FORM_ENCODING =
            "@Field parameters can only be used with form encoding"
        const val FIELD_MAP_PARAMETERS_CAN_ONLY_BE_USED_WITH_FORM_ENCODING =
            "@FieldMap parameters can only be used with form encoding"
        const val PART_MAP_PARAMETER_TYPE_MUST_BE_MAP = "@PartMap parameter type must be Map."
        const val PART_PARAMETER_TYPE_MAY_NOT_BE_NULLABLE = "Part parameter type may not be nullable"
        const val PATH_PARAMETER_TYPE_MAY_NOT_BE_NULLABLE = "Path parameter type may not be nullable"
        const val BODY_PARAMETER_TYPE_MAY_NOT_BE_NULLABLE = "Body parameter type may not be nullable"
        const val API_DECLARATIONS_MUST_BE_INTERFACES = "API declarations must be interfaces."

        const val PATH_CAN_ONLY_BE_USED_WITH_RELATIVE_URL_ON = "@Path can only be used with relative url on "
        const val NON_BODY_HTTP_METHOD_CANNOT_CONTAIN_BODY = "Non-body HTTP method cannot contain @Body"
        const val BODY_PARAMETERS_CANNOT_BE_USED_WITH_FORM_OR_MULTI_PART_ENCODING =
            "@Body parameters cannot be used with form or multi-part encoding"
        const val FOR_STREAMING_THE_RETURN_TYPE_MUST_BE_HTTP_STATEMENT =
            "For streaming the return type must be io.ktor.client.statement.HttpStatement"
        const val COULD_NOT_FIND_ANY_KTORFIT_ANNOTATIONS_IN_CLASS = "Could not find any Ktorfit annotations in class"
        fun MISSING_EITHER_KEYWORD_URL_OrURL_PARAMETER(keyword: String) =
            "Missing either @$keyword URL or @Url parameter"

        fun NO_KTORFIT_ANNOTATION_FOUND_AT(parameterName: String): String {
            return "No Ktorfit Annotation found at $parameterName"
        }

        fun MISSING_X_IN_RELATIVE_URL_PATH(keyword: String) = "Missing {${keyword}} in relative url path"
        const val JAVA_INTERFACES_ARE_NOT_SUPPORTED = "Java Interfaces are not supported"
        const val INTERNAL_INTERFACES_ARE_NOT_SUPPORTED = "internal Interfaces are not supported"
        const val INTERFACE_NEEDS_TO_HAVE_A_PACKAGE = "Interface needs to have a package"
        const val ONLY_ONE_HTTP_METHOD_IS_ALLOWED = "Only one HTTP method is allowed."
        const val FORM_URL_ENCODED_CAN_ONLY_BE_SPECIFIED_ON_HTTP_METHODS_WITH_REQUEST_BODY =
            "FormUrlEncoded can only be specified on HTTP methods with request body (e.g., @POST)."
        const val ONLY_ONE_ENCODING_ANNOTATION_IS_ALLOWED = "Only one encoding annotation is allowed."
        const val MULTIPLE_URL_METHOD_ANNOTATIONS_FOUND = "Multiple @Url method annotations found"
        const val TYPE_PARAMETERS_ARE_UNSUPPORTED_ON = "Type parameters are unsupported on "
        const val MULTIPART_CAN_ONLY_BE_SPECIFIED_ON_HTTPMETHODS =
            "Multipart can only be specified on HTTP methods with request body (e.g., @POST)"
        const val VARARG_NOT_SUPPORTED_USE_LIST_OR_ARRAY = "vararg not supported use List or Array"
        const val NULLABLE_PARAMETERS_ARE_NOT_SUPPORTED = "Nullable Parameters Are Not Supported"
        fun NO_HTTP_ANNOTATION_AT(functionName: String) = "No Http annotation at $functionName"
        fun URL_CAN_ONLY_BE_USED_WITH_EMPY(keyword: String) = "@Url can only be used with empty @${keyword} URL value"
        const val HEADERS_VALUE_MUST_BE_IN_FORM = "@Headers value must be in the form \"Name: Value\". Found: "


    }
}

public fun KSPLogger.ktorfitError(s: String, classDec: KSNode) {
    this.error("Ktorfit: $s", classDec)
}