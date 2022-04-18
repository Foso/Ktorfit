import com.google.devtools.ksp.closestClassDeclaration
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.processing.Dependencies.Companion.ALL_FILES
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import de.jensklingenberg.ktorfit.generator.generateClassImpl
import de.jensklingenberg.ktorfit.generator.generateKtorfitExtSource
import de.jensklingenberg.ktorfit.http.*
import de.jensklingenberg.ktorfit.ktorfitError
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

    private lateinit var myResolver: Resolver

    override fun process(resolver: Resolver): List<KSAnnotated> {
        myResolver = resolver

        if (invoked) {
            return emptyList()
        }
        invoked = true

        return emptyList()
    }

    private fun getAnnotatedFunctions(): List<KSFunctionDeclaration> {
        val getAnnotated = myResolver.getSymbolsWithAnnotation(GET::class.java.name).toList()
        val postAnnotated = myResolver.getSymbolsWithAnnotation(POST::class.java.name).toList()
        val putAnnotated = myResolver.getSymbolsWithAnnotation(PUT::class.java.name).toList()
        val deleteAnnotated = myResolver.getSymbolsWithAnnotation(DELETE::class.java.name).toList()
        val headAnnotated = myResolver.getSymbolsWithAnnotation(HEAD::class.java.name).toList()
        val optionsAnnotated = myResolver.getSymbolsWithAnnotation(OPTIONS::class.java.name).toList()
        val patchAnnotated = myResolver.getSymbolsWithAnnotation(PATCH::class.java.name).toList()
        val httpAnnotated = myResolver.getSymbolsWithAnnotation(HTTP::class.java.name).toList()

        val ksAnnotatedList =
            getAnnotated + postAnnotated + putAnnotated + deleteAnnotated + headAnnotated + optionsAnnotated + patchAnnotated + httpAnnotated
        return ksAnnotatedList.map { it as KSFunctionDeclaration }
    }


    override fun finish() {

        val myClasses = getAnnotatedFunctions().groupBy { it.closestClassDeclaration()!! }
            .map { (classDec) ->
                if (classDec.origin.name == "JAVA") {
                    logger.ktorfitError("Java Interfaces are not supported", classDec)
                }
                if (classDec.typeParameters.isNotEmpty()) {
                    logger.ktorfitError(
                        "Type parameters are unsupported on ${classDec.simpleName.asString()}",
                        classDec
                    )
                }
                toClassData(classDec, logger)
            }

        generateClassImpl(myClasses, codeGenerator)


        val source = generateKtorfitExtSource(myClasses, env.platforms.any { it.platformName == "JS" })
        codeGenerator.createNewFile(ALL_FILES, "de.jensklingenberg.ktorfit", "KtorfitExt", "kt").use { output ->
            OutputStreamWriter(output).use { writer ->
                writer.write(source)
            }
        }

    }
}

