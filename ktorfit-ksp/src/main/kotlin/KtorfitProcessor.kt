import com.google.devtools.ksp.closestClassDeclaration
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.processing.Dependencies.Companion.ALL_FILES
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import de.jensklingenberg.ktorfit.generator.generateImplClass
import de.jensklingenberg.ktorfit.generator.generateKtorfitExtSource
import de.jensklingenberg.ktorfit.http.*
import de.jensklingenberg.ktorfit.ktorfitError
import de.jensklingenberg.ktorfit.model.KtorfitError.Companion.JAVA_INTERFACES_ARE_NOT_SUPPORTED
import de.jensklingenberg.ktorfit.model.KtorfitError.Companion.TYPE_PARAMETERS_ARE_UNSUPPORTED_ON
import de.jensklingenberg.ktorfit.parser.toClassData
import java.io.OutputStreamWriter


public class KtorfitProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return KtorfitProcessor(environment)
    }
}

public class KtorfitProcessor(private val env: SymbolProcessorEnvironment) : SymbolProcessor {
    private val codeGenerator: CodeGenerator = env.codeGenerator
    private val logger: KSPLogger = env.logger
    private var invoked = false

    private lateinit var resolver: Resolver

    override fun process(resolver: Resolver): List<KSAnnotated> {
        this.resolver = resolver

        if (invoked) {
            return emptyList()
        }
        invoked = true

        return emptyList()
    }

    private fun getAnnotatedFunctions(): List<KSFunctionDeclaration> {
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


    override fun finish() {

        val classDataList = getAnnotatedFunctions().groupBy { it.closestClassDeclaration()!! }
            .map { (classDec) ->
                if (classDec.origin.name == "JAVA") {
                    logger.ktorfitError(JAVA_INTERFACES_ARE_NOT_SUPPORTED, classDec)
                }
                if (classDec.typeParameters.isNotEmpty()) {
                    logger.ktorfitError(
                        TYPE_PARAMETERS_ARE_UNSUPPORTED_ON+" ${classDec.simpleName.asString()}",
                        classDec
                    )
                }
                toClassData(classDec, logger)
            }

        generateImplClass(classDataList, codeGenerator)


        val source = generateKtorfitExtSource(classDataList, env.platforms.any { it.platformName == "JS" })
        codeGenerator.createNewFile(ALL_FILES, "de.jensklingenberg.ktorfit", "KtorfitExt", "kt").use { output ->
            OutputStreamWriter(output).use { writer ->
                writer.write(source)
            }
        }

    }
}

