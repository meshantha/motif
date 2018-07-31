package motif.compiler.codegen

import com.squareup.javapoet.CodeBlock
import com.squareup.javapoet.MethodSpec
import motif.compiler.model.Dependency
import motif.compiler.model.ParentInterfaceMethod
import javax.lang.model.element.Modifier

class BridgeParentImplMethodSpec(
        parent: BridgeParentSpec,
        bridgeParameters: BridgeMethodParametersSpec,
        parentMethod: ParentInterfaceMethod) {

    val dependency: Dependency = parentMethod.dependency

    val returnStatement: CodeBlock = bridgeParameters[dependency]?.let { parameter ->
        parameter.spreadMethodName?.let {
            CodeBlock.of("return \$N.\$N()", parameter.spec, it)
        } ?: CodeBlock.of("return \$N", parameter.spec)
    } ?: parent.provisionMethods[dependency]?.let { parentMethod ->
        CodeBlock.of("return \$N.\$N()", bridgeParameters.parentParameter, parentMethod)
    } ?: throw RuntimeException("Dependency should always be provided by either parameters or parent.")

    val spec: MethodSpec = MethodSpec.methodBuilder(parentMethod.name).apply {
        addAnnotation(Override::class.java)
        addModifiers(Modifier.PUBLIC)
        returns(dependency.className)
        addStatement(returnStatement)
    }.build()
}