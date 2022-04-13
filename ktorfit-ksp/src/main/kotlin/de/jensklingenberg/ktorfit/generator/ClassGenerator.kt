package de.jensklingenberg.ktorfit.generator

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import de.jensklingenberg.ktorfit.model.MyClass
import de.jensklingenberg.ktorfit.model.MyFunction
import de.jensklingenberg.ktorfit.node.*
import java.io.OutputStreamWriter


fun generateClassImpl(myClasses: List<MyClass>, codeGenerator: CodeGenerator) {
    myClasses.forEach { myClass ->
        val packageName = myClass.packageName
        val className = myClass.name

        val funcCmds = getFuncNodes(myClass.functions)

        val classImplSource = ClassNode(myClass, funcCmds).toString()

        codeGenerator.createNewFile(Dependencies.ALL_FILES, packageName, "_${className}Impl", "kt").use { output ->
            OutputStreamWriter(output).use { writer ->
                writer.write(classImplSource)
            }
        }
    }
}

private fun getFuncNodes(functions: List<MyFunction>): List<FuncNode> {
    return functions.map { myFunction ->
        val fieldArgumentsNode = FieldArgumentsNode(myFunction.params)
        val partsArgumentNode = PartsArgumentNode(myFunction.params)
        val bodyDataArgumentCmd = BodyDataArgumentNode(myFunction.params)
        val headerArgumentCmd = HeadersArgumentNode(myFunction.annotations, myFunction.params)
        val urlArgumentNode = RelativeUrlArgumentNode(myFunction)
        val queryArgumentNode = QueryArgumentNode(myFunction.params)
        val requestBuilderArgumentNode = RequestBuilderArgumentNode(myFunction.params)

        val argumentCmd = RequestDataArgumentNode(
            myFunction,
            headerArgumentCmd,
            bodyDataArgumentCmd,
            partsArgumentNode,
            urlArgumentNode,
            queryArgumentNode,
            fieldArgumentsNode,
            requestBuilderArgumentNode,
            myFunction.httpMethodAnnotation
        )

        val bodyCmd = FuncBodyNode(myFunction, argumentCmd)
        val paramCmds = myFunction.params.map { ParamNode(it) }

        FuncNode(myFunction, bodyCmd, paramCmds)
    }
}


