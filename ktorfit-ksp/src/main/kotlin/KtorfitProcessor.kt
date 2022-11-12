import com.google.auto.service.AutoService
import com.google.devtools.ksp.closestClassDeclaration
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import de.jensklingenberg.ktorfit.generator.generateImplClass
import de.jensklingenberg.ktorfit.generator.generateKtorfitExtClass
import de.jensklingenberg.ktorfit.http.*

import de.jensklingenberg.ktorfit.model.toClassData

@AutoService(SymbolProcessorProvider::class)
public class KtorfitProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return KtorfitProcessor(environment)
    }
}

public class KtorfitProcessor(private val env: SymbolProcessorEnvironment) : SymbolProcessor {
    private val codeGenerator: CodeGenerator = env.codeGenerator
    private val logger: KSPLogger = env.logger
    private var invoked = false


    companion object {
        lateinit var ktorfitResolver: Resolver
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
       val type = (env.options.get("Ktorfit_Errors")?.toIntOrNull()) ?: 1
        ktorfitResolver = resolver

        if (invoked) {
            return emptyList()
        }
        invoked = true

        val classDataList = getAnnotatedFunctions(ktorfitResolver).groupBy { it.closestClassDeclaration()!! }
            .map { (classDec) ->
                classDec.toClassData( KtorfitLogger(logger,type))
            }

        generateImplClass(classDataList, codeGenerator)

        if (classDataList.isNotEmpty()) {
            generateKtorfitExtClass(classDataList, env.platforms.any { it.platformName == "JS" }, codeGenerator)
        }

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
            getAnnotated + postAnnotated + putAnnotated + deleteAnnotated + headAnnotated + optionsAnnotated + patchAnnotated + httpAnnotated
        return ksAnnotatedList.map { it as KSFunctionDeclaration }
    }
}

