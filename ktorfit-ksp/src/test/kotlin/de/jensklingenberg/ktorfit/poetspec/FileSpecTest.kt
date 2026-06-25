package de.jensklingenberg.ktorfit.poetspec

import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import de.jensklingenberg.ktorfit.model.ClassData
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock

class FileSpecTest {
    @Test
    fun `when createRegistration is true, generated file contains KtorfitFactoryRegistry import`() {
        val classData = createTestClassData()
        val implClassSpec = TypeSpec.classBuilder(classData.implName).build()
        val fileSpec = createFileSpec(classData, classData.implName, implClassSpec, createRegistration = true)
        val generatedCode = fileSpec.toString()

        assertTrue(
            generatedCode.contains("import de.jensklingenberg.ktorfit.optins.KtorfitFactoryRegistry"),
        )
    }

    @Test
    fun `when createRegistration is false, generated file does NOT contain KtorfitFactoryRegistry import`() {
        val classData = createTestClassData()
        val implClassSpec = TypeSpec.classBuilder(classData.implName).build()
        val fileSpec = createFileSpec(classData, classData.implName, implClassSpec, createRegistration = false)
        val generatedCode = fileSpec.toString()

        assertTrue(!generatedCode.contains("KtorfitFactoryRegistry"))
    }

    @Test
    fun `when createRegistration is true, generated file contains self-registration property`() {
        val classData = createTestClassData()
        val implClassSpec = TypeSpec.classBuilder(classData.implName).build()
        val fileSpec = createFileSpec(classData, classData.implName, implClassSpec, createRegistration = true)
        val generatedCode = fileSpec.toString()

        assertTrue(generatedCode.contains("KtorfitFactoryRegistry.register(TestService::class, _TestServiceProvider())"))
    }

    @Test
    fun `when createRegistration is false, generated file does NOT contain registration property`() {
        val classData = createTestClassData()
        val implClassSpec = TypeSpec.classBuilder(classData.implName).build()
        val fileSpec = createFileSpec(classData, classData.implName, implClassSpec, createRegistration = false)
        val generatedCode = fileSpec.toString()

        assertTrue(!generatedCode.contains("__ktorfit_registration"))
        assertTrue(!generatedCode.contains("KtorfitFactoryRegistry.register"))
    }

    @Test
    fun `generated file contains provider class regardless of registration flag`() {
        val classData = createTestClassData()
        val implClassSpec = TypeSpec.classBuilder(classData.implName).build()
        val fileSpec = createFileSpec(classData, classData.implName, implClassSpec, createRegistration = false)
        val generatedCode = fileSpec.toString()

        assertTrue(generatedCode.contains("class _TestServiceProvider"))
    }

    @Test
    fun `generated file contains create extension function regardless of registration flag`() {
        val classData = createTestClassData()
        val implClassSpec = TypeSpec.classBuilder(classData.implName).build()
        val fileSpec = createFileSpec(classData, classData.implName, implClassSpec, createRegistration = false)
        val generatedCode = fileSpec.toString()

        assertTrue(generatedCode.contains("fun Ktorfit.createTestService()"))
    }

    @Test
    fun `registration property is private and suppresses unused warning`() {
        val classData = createTestClassData()
        val implClassSpec = TypeSpec.classBuilder(classData.implName).build()
        val fileSpec = createFileSpec(classData, classData.implName, implClassSpec, createRegistration = true)
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
