package de.jensklingenberg.ktorfit

import com.google.devtools.ksp.closestClassDeclaration
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import de.jensklingenberg.ktorfit.generator.generateImplClass
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.HEAD
import de.jensklingenberg.ktorfit.http.HTTP
import de.jensklingenberg.ktorfit.http.OPTIONS
import de.jensklingenberg.ktorfit.http.PATCH
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.model.toClassData

class KtorfitProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        KtorfitProcessor(environment, KtorfitOptions(environment.options))
}

class KtorfitProcessor(
    private val env: SymbolProcessorEnvironment,
    private val ktorfitOptions: KtorfitOptions
) : SymbolProcessor {
    private var invoked = false

    companion object {
        lateinit var ktorfitResolver: Resolver
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val loggingType = ktorfitOptions.errorsLoggingType
        ktorfitResolver = resolver
        if (invoked) {
            return emptyList()
        }
        invoked = true

        val classDataList =
            getAnnotatedFunctions(ktorfitResolver)
                .groupBy { it.closestClassDeclaration() }
                .map { (classDec) ->
                    classDec?.toClassData(KtorfitLogger(env.logger, loggingType))
                }.mapNotNull { it }

        generateImplClass(classDataList, env.codeGenerator, resolver, ktorfitOptions)

        return emptyList()
    }

    /**
     * Returns a list of all [KSFunctionDeclaration] which are annotated with a Http Method Annotation
     */
    private fun getAnnotatedFunctions(resolver: Resolver): List<KSFunctionDeclaration> {
        val getAnnotated = resolver.getSymbolsWithAnnotation(GET::class.java.name).toList()
        val postAnnotated = resolver.getSymbolsWithAnnotation(POST::class.java.name).toList()
        val putAnnotated = resolver.getSymbolsWithAnnotation(PUT::class.java.name).toList()
        val deleteAnnotated = resolver.getSymbolsWithAnnotation(DELETE::class.java.name).toList()
        val headAnnotated = resolver.getSymbolsWithAnnotation(HEAD::class.java.name).toList()
        val optionsAnnotated = resolver.getSymbolsWithAnnotation(OPTIONS::class.java.name).toList()
        val patchAnnotated = resolver.getSymbolsWithAnnotation(PATCH::class.java.name).toList()
        val httpAnnotated = resolver.getSymbolsWithAnnotation(HTTP::class.java.name).toList()

        val ksAnnotatedList =
            getAnnotated +
                postAnnotated +
                putAnnotated +
                deleteAnnotated +
                headAnnotated +
                optionsAnnotated +
                patchAnnotated +
                httpAnnotated
        return ksAnnotatedList.filterIsInstance<KSFunctionDeclaration>()
    }
}
