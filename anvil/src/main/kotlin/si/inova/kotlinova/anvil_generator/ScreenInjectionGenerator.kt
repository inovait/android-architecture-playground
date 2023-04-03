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
import com.squareup.anvil.compiler.internal.reference.ParameterReference
import com.squareup.anvil.compiler.internal.reference.asClassName
import com.squareup.anvil.compiler.internal.reference.asTypeName
import com.squareup.anvil.compiler.internal.reference.classAndInnerClassReferences
import com.squareup.anvil.compiler.internal.safePackageString
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeSpec
import dagger.Binds
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

         val screenType = it.getScreenKeyIfItExists() ?: return@mapNotNull null

         generateScreenFactory(codeGenDir, it, screenType)
      }.flatten().toList()
   }

   private fun generateScreenFactory(
      codeGenDir: File,
      clas: ClassReference.Psi,
      screenKeyType: ClassReference
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
         .addMember("%T::class", screenKeyType.asTypeName())
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
         .returns(className)
         .addAnnotation(Provides::class)
         .addStatement(
            "return %T(${constructorParameters.joinToString { "%L" }})",
            className,
            *constructorParameters.map { it.name }.toTypedArray()
         )
         .build()

      val bindsScreenFunction = FunSpec.builder("bindsScreen")
         .addModifiers(KModifier.ABSTRACT)
         .returns(screenClassName)
         .addAnnotation(Binds::class)
         .addAnnotation(IntoMap::class)
         .addParameter("screen", className)
         .addAnnotation(classKeyAnnotation)
         .build()

      val scopedServiceParameters = getRequiredScopedServices(constructorParameters)

      val providesServiceListFunction = if (!screenKeyType.isAbstract()) {
         FunSpec.builder("providesScreenRegistration")
            .returns(SCREEN_REGISTRATION.parameterizedBy(STAR))
            .addAnnotation(Provides::class)
            .addAnnotation(IntoMap::class)
            .addAnnotation(screenKeyClassKeyAnnotation)
            .addStatement(
               "return %T(%T::class.java, ${scopedServiceParameters.joinToString { "%T::class.java" }})",
               SCREEN_REGISTRATION,
               className,
               *scopedServiceParameters.map { it.type().asTypeName() }.toTypedArray()
            )
            .build()
      } else {
         null
      }

      val content = FileSpec.buildFile(
         packageName = packageName,
         fileName = outputFileName,
         generatorComment = "Automatically generated file. DO NOT MODIFY!"
      ) {
         val companionObject = TypeSpec.companionObjectBuilder()
            .addFunction(providesScreenFunction)
            .also { if (providesServiceListFunction != null) it.addFunction(providesServiceListFunction) }
            .build()

         val moduleInterfaceSpec = TypeSpec.classBuilder(outputFileName)
            .addAnnotation(Module::class)
            .addModifiers(KModifier.ABSTRACT)
            .addAnnotation(contributesToAnnotation)
            .addFunction(bindsScreenFunction)
            .addType(companionObject)
            .build()

         addType(moduleInterfaceSpec)
      }

      return listOf(
         createGeneratedFile(codeGenDir, packageName, outputFileName, content)
      )
   }

   private fun getRequiredScopedServices(constructorParameters: List<ParameterReference.Psi>): List<ParameterReference> {
      val constructorsOfAllNestedScreens = constructorParameters
         .map { it.type().asClassReference() }
         .filter { it.getScreenKeyIfItExists() != null }
         .map { it.constructors.firstOrNull()?.parameters ?: emptyList() }

      val mergedConstructors = constructorsOfAllNestedScreens + listOf(constructorParameters)
      return mergedConstructors.flatMap { constructor ->
         constructor.filter { it.type().isScopedService() }
      }
   }

   private fun ClassReference.getScreenKeyIfItExists(initialPassedKeyType: ClassReference? = null): ClassReference? {
      for (superReference in directSuperTypeReferences()) {

         val passedKeyType = initialPassedKeyType ?: superReference.unwrappedTypes
            .mapNotNull { it.asClassReferenceOrNull() }
            .firstOrNull { it.isKey() }

         val superClassReference = superReference.asClassReference()

         if (passedKeyType != null && superClassReference.asClassName() == SCREEN_BASE_CLASS) {
            return passedKeyType
         } else {
            superClassReference.getScreenKeyIfItExists(passedKeyType)?.let { return it }
         }
      }

      return null
   }

   private fun ClassReference.isKey(): Boolean {
      for (superReference in directSuperTypeReferences()) {
         val superClassReference = superReference.asClassReference()

         if (superClassReference.asClassName() == SCREEN_KEY_BASE_CLASS) {
            return true
         } else {
            superClassReference.isKey().let { if (it) return true }
         }
      }

      return false
   }

   override fun isApplicable(context: AnvilContext): Boolean = true
}
