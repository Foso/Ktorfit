package de.jensklingenberg.ktorfit.poetspec

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import de.jensklingenberg.ktorfit.model.ClassData
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock

class FileSpecTest {

    @Test
    fun `generated file contains KtorfitApiRegistry import`() {
        val classData = createTestClassData()
        val implClassSpec = TypeSpec.classBuilder(classData.implName).build()
        val fileSpec = createFileSpec(classData, classData.implName, implClassSpec)
        val generatedCode = fileSpec.toString()

        // KotlinPoet wraps "internal" in backticks because it's a reserved word
        assertTrue(
            generatedCode.contains("import de.jensklingenberg.ktorfit.`internal`.KtorfitApiRegistry"),
        )
    }

    @Test
    fun `generated file contains self-registration property with correct class name`() {
        val classData = createTestClassData()
        val implClassSpec = TypeSpec.classBuilder(classData.implName).build()
        val fileSpec = createFileSpec(classData, classData.implName, implClassSpec)
        val generatedCode = fileSpec.toString()

        assertTrue(generatedCode.contains("KtorfitApiRegistry.register(TestService::class)"))
    }

    @Test
    fun `self-registration references the create extension function`() {
        val classData = createTestClassData()
        val implClassSpec = TypeSpec.classBuilder(classData.implName).build()
        val fileSpec = createFileSpec(classData, classData.implName, implClassSpec)
        val generatedCode = fileSpec.toString()

        assertTrue(generatedCode.contains("ktorfit.createTestService()"))
    }

    @Test
    fun `registration property is private and suppresses unused warning`() {
        val classData = createTestClassData()
        val implClassSpec = TypeSpec.classBuilder(classData.implName).build()
        val fileSpec = createFileSpec(classData, classData.implName, implClassSpec)
        val generatedCode = fileSpec.toString()

        assertTrue(generatedCode.contains("@Suppress(\"unused\")"))
        assertTrue(generatedCode.contains("private val __ktorfit_registration"))
    }

    private fun createTestClassData(): ClassData {
        val mockFile = mock<KSFile>()
        return ClassData(
            name = "TestService",
            packageName = "com.example.api",
            functions = emptyList(),
            imports = emptySet(),
            superClasses = emptyList(),
            properties = emptyList(),
            modifiers = listOf(KModifier.PUBLIC),
            ksFile = mockFile,
            annotations = emptyList(),
        )
    }
}
