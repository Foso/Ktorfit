package de.jensklingenberg.ktorfit.converter.builtin

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.Converter
import de.jensklingenberg.ktorfit.converter.KtorfitResult
import de.jensklingenberg.ktorfit.converter.TypeData
import io.ktor.client.statement.HttpResponse

public class DontSwallowExceptionsConverterFactory : Converter.Factory {
    private class DefaultSuspendResponseConverter(
        val typeData: TypeData
    ) : Converter.SuspendResponseConverter<HttpResponse, Any?> {
        override suspend fun convert(result: KtorfitResult): Any? =
            when (result) {
                is KtorfitResult.Failure -> {
                    throw result.throwable
                }

                is KtorfitResult.Success -> {
                    result.response.call.body(typeData.typeInfo)
                }
            }
    }

    override fun suspendResponseConverter(
        typeData: TypeData,
        ktorfit: Ktorfit,
    ): Converter.SuspendResponseConverter<HttpResponse, *> = DefaultSuspendResponseConverter(typeData)
}
