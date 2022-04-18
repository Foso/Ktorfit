package de.jensklingenberg.ktorfit.model

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
        const val QUERY_MAP_PARAMETER_TYPE_MAY_NOT_BE_NULLABLE = "QueryMap parameter type may not be nullable"
        const val FIELD_MAP_PARAMETER_TYPE_MUST_BE_MAP = "@FieldMap parameter type must be Map."
        const val FIELD_MAP_KEYS_MUST_BE_OF_TYPE_STRING = "@FieldMap keys must be of type String:"
        const val ONLY_ONE_REQUEST_BUILDER_IS_ALLOWED = "Only one RequestBuilder is allowed."
        const val REQ_BUILDER_PARAMETER_TYPE_NEEDS_TO_BE_HTTP_REQUEST_BUILDER = "@ReqBuilder parameter type needs to be HttpRequestBuilder.()->Unit"
        const val FIELD_PARAMETERS_CAN_ONLY_BE_USED_WITH_FORM_ENCODING = "@Field parameters can only be used with form encoding"
        const val FIELD_MAP_PARAMETERS_CAN_ONLY_BE_USED_WITH_FORM_ENCODING = "@FieldMap parameters can only be used with form encoding"

    }
}