package de.jensklingenberg.ktorfit.model

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import de.jensklingenberg.ktorfit.model.annotations.HttpMethodAnnotation
import de.jensklingenberg.ktorfit.model.annotations.ParameterAnnotation

class RequestBuilderValidator(val functionParameters: List<ParameterData>, val httpMethodAnnoList: List<HttpMethodAnnotation>, val functionName: String) : Validator {
    override
    fun validateFunctionData(
        functionData: KSFunctionDeclaration,
        logger: KSPLogger,
    ) {
        if (functionParameters.filter { it.hasAnnotation<ParameterAnnotation.RequestBuilder>() }.size > 1) {
            logger.error(
                KtorfitError.ONLY_ONE_REQUEST_BUILDER_IS_ALLOWED + " Found: " + httpMethodAnnoList.joinToString { it.toString() } + " at " +
                        functionName,
                functionData,
            )
        }
    }
}