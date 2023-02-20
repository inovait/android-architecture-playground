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
import com.squareup.anvil.compiler.internal.reference.TypeReference
import com.squareup.anvil.compiler.internal.reference.asClassName
import com.squareup.anvil.compiler.internal.reference.classAndInnerClassReferences
import com.squareup.anvil.compiler.internal.safePackageString
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
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
         if (it.isAbstract()) {
            return@mapNotNull null
         }

         val screenType = it.getScreenTypeIfItExists() ?: return@mapNotNull null

         generateScreenFactory(codeGenDir, it, screenType)
      }.flatten().toList()
   }

   private fun generateScreenFactory(
      codeGenDir: File,
      clas: ClassReference.Psi,
      screenType: TypeReference
   ): Collection<GeneratedFile> {
      val className = clas.asClassName()
      val packageName = clas.packageFqName.safePackageString(
         dotPrefix = false,
         dotSuffix = false,
      )
      val outputFileName = className.simpleName + "Module"

      val contributesToAnnotation = AnnotationSpec.builder(ContributesTo::class)
         .addMember("%T::class", ACTIVITY_SCOPE_ANNOTATION)
         .build()

      val classKeyAnnotation = AnnotationSpec.builder(ClassKey::class)
         .addMember("%T::class", className)
         .build()

      val screenKeyClassKeyAnnotation = AnnotationSpec.builder(ClassKey::class)
         .addMember("%T::class", screenType.unwrappedTypes.first().asTypeName())
         .build()

      val screenClassName = SCREEN_BASE_CLASS.parameterizedBy(STAR)

      val constructorParameters = clas.constructors.firstOrNull()?.parameters ?: emptyList()

      val providesScreenFunction = FunSpec.builder("providesScreen")
         .apply {
            addParameters(constructorParameters.map { parameter ->
               ParameterSpec.builder(parameter.name, parameter.type().asTypeName())
                  .apply {
                     for (annotation in parameter.annotations) {
                        addAnnotation(annotation.toAnnotationSpec())
                     }

                     if (parameter.type().isScopedService()) {
                        addAnnotation(SIMPLE_STACK_SCOPED_ANNOTATION)
                     }
                  }
                  .build()
            })
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

      val scopedServiceParameters = constructorParameters.filter { it.type().isScopedService() }

      val providesServiceListFunction = FunSpec.builder("providesScopedServiceList")
         .returns(List::class.asTypeName().parameterizedBy(Class::class.asTypeName().parameterizedBy(STAR)))
         .addAnnotation(Provides::class)
         .addAnnotation(IntoMap::class)
         .addAnnotation(screenKeyClassKeyAnnotation)
         .addStatement(
            "return %L(${scopedServiceParameters.joinToString { "%T::class.java" }})",
            "listOf",
            *scopedServiceParameters.map { it.type().asTypeName() }.toTypedArray()
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
            .addFunction(providesScreenFunction)
            .addFunction(providesServiceListFunction)
            .build()

         addType(moduleInterfaceSpec)
      }

      return listOf(
         createGeneratedFile(codeGenDir, packageName, outputFileName, content)
      )
   }

   private fun ClassReference.getScreenTypeIfItExists(): TypeReference? {
      for (superReference in directSuperTypeReferences()) {
         val superClassReference = superReference.asClassReference()

         if (superClassReference.asClassName() == SCREEN_BASE_CLASS) {
            return superReference
         } else {
            superClassReference.getScreenTypeIfItExists()?.let { return it }
         }
      }

      return null
   }

   override fun isApplicable(context: AnvilContext): Boolean = true
}


