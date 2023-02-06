package si.inova.kotlinova.anvil_generator

import com.google.auto.service.AutoService
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.ExperimentalAnvilApi
import com.squareup.anvil.compiler.api.AnvilContext
import com.squareup.anvil.compiler.api.CodeGenerator
import com.squareup.anvil.compiler.api.GeneratedFile
import com.squareup.anvil.compiler.api.createGeneratedFile
import com.squareup.anvil.compiler.internal.buildFile
import com.squareup.anvil.compiler.internal.reference.ClassReference
import com.squareup.anvil.compiler.internal.reference.asClassName
import com.squareup.anvil.compiler.internal.reference.classAndInnerClassReferences
import com.squareup.anvil.compiler.internal.safePackageString
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeSpec
import dagger.Module
import dagger.Provides
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.psi.KtFile
import java.io.File

@Suppress("unused")
@OptIn(ExperimentalAnvilApi::class)
@AutoService(CodeGenerator::class)
class ScreenInjectionGenerator : CodeGenerator {
   override fun generateCode(
      codeGenDir: File,
      module: ModuleDescriptor,
      projectFiles: Collection<KtFile>
   ): Collection<GeneratedFile> {
      return projectFiles.classAndInnerClassReferences(module).mapNotNull {
         if (!it.directSuperTypeReferences()
               .any { superType ->
                  superType.asClassReference().fqName.asString() == SCREEN_TYPE
               }
         ) {
            return@mapNotNull null
         }

         generateScreenFactory(codeGenDir, it)
      }.flatten().toList()
   }

   private fun generateScreenFactory(codeGenDir: File, clas: ClassReference.Psi): Collection<GeneratedFile> {
      val className = clas.asClassName()
      val packageName = clas.packageFqName.safePackageString(
         dotPrefix = false,
         dotSuffix = false,
      )
      val outputFileName = className.simpleName + "Module"

      val contributesToAnnotation = AnnotationSpec.builder(ContributesTo::class)
         .addMember("%T::class", ClassName("com.deliveryhero.whetstone.app", "ApplicationScope"))
         .build()

      val classKeyAnnotation = AnnotationSpec.builder(ClassKey::class)
         .addMember("%T::class", className)
         .build()

      val screenClassName = ClassName("si.inova.androidarchitectureplayground.screens", "Screen")
         .parameterizedBy(STAR)

      val constructorParameters = clas.constructors.firstOrNull()?.parameters ?: emptyList()

      val providesFunction = FunSpec.builder("provides")
         .apply {
            for (parameter in constructorParameters) {
               addParameter(parameter.name, parameter.type().asTypeName())
            }
         }
         .returns(screenClassName)
         .addAnnotation(Provides::class)
         .addAnnotation(IntoMap::class)
         .addAnnotation(classKeyAnnotation)
         .addStatement(
            "return %T(${constructorParameters.joinToString { "%L" }})",
            className,
            *constructorParameters.map { it.name }.toTypedArray()
         )
         .build()

      val content = FileSpec.buildFile(
         packageName = packageName,
         fileName = outputFileName,
         generatorComment = "Automatically generated file. DO NOT MODIFY!"
      ) {
         val moduleInterfaceSpec = TypeSpec.classBuilder(outputFileName)
            .addAnnotation(Module::class)
            .addAnnotation(contributesToAnnotation)
            .addFunction(providesFunction)
            .build()

         addType(moduleInterfaceSpec)
      }

      return listOf(
         createGeneratedFile(codeGenDir, packageName, outputFileName, content)
      )
   }

   override fun isApplicable(context: AnvilContext): Boolean = true
}

private const val SCREEN_TYPE = "si.inova.androidarchitectureplayground.screens.Screen"
