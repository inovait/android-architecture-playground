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
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.psi.KtFile
import java.io.File

@OptIn(ExperimentalAnvilApi::class)
@Suppress("unused")
@AutoService(CodeGenerator::class)
class ScopedServiceInjectionGenerator : CodeGenerator {
   override fun generateCode(
      codeGenDir: File,
      module: ModuleDescriptor,
      projectFiles: Collection<KtFile>
   ): Collection<GeneratedFile> {
      return projectFiles.classAndInnerClassReferences(module).mapNotNull {
         if (it.isAbstract() || !it.isScopedService()) {
            return@mapNotNull null
         }

         generateScopedServiceModule(codeGenDir, it)
      }.flatten().toList()
   }

   private fun generateScopedServiceModule(
      codeGenDir: File,
      clas: ClassReference.Psi
   ): Collection<GeneratedFile> {
      val className = clas.asClassName()
      val packageName = clas.packageFqName.safePackageString(
         dotPrefix = false,
         dotSuffix = false,
      )
      val outputFileName = className.simpleName + "Module"

      val contributesToAnnotation = AnnotationSpec.builder(ContributesTo::class)
         .addMember("%T::class", ClassName("com.deliveryhero.whetstone.app", "ApplicationScope"))
         .build()

      val backstackContributesToAnnotation = AnnotationSpec.builder(ContributesTo::class)
         .addMember("%T::class", ClassName("si.inova.androidarchitectureplayground.di", "SimpleStackActivityScope"))
         .build()

      val classKeyAnnotation = AnnotationSpec.builder(ClassKey::class)
         .addMember("%T::class", className)
         .build()

      val scopedServiceClassName = ClassName("si.inova.androidarchitectureplayground.screens", "ScopedService")
      val backstackClassName = ClassName("com.zhuinden.simplestack", "Backstack")

      val bindsServiceFunction = FunSpec.builder("bindConstructor")
         .returns(scopedServiceClassName)
         .addParameter("service", className)
         .addAnnotation(Binds::class)
         .addAnnotation(IntoMap::class)
         .addAnnotation(classKeyAnnotation)
         .addModifiers(KModifier.ABSTRACT)
         .build()

      val provideFromSimpleStackFunction = FunSpec.builder("provideFromSimpleStack")
         .returns(className)
         .addParameter("backstack", backstackClassName)
         .addAnnotation(Provides::class)
         .addAnnotation(ClassName("si.inova.androidarchitectureplayground.screens", "SimpleStackScoped"))
         .addCode("return backstack.lookupService(%T::class.java.name)", className)
         .build()

      val fromBackstackProviderModule = TypeSpec.classBuilder(className.simpleName + "BackstackModule")
         .addAnnotation(Module::class)
         .addAnnotation(backstackContributesToAnnotation)
         .addFunction(provideFromSimpleStackFunction)
         .build()

      val content = FileSpec.buildFile(
         packageName = packageName,
         fileName = outputFileName,
         generatorComment = "Automatically generated file. DO NOT MODIFY!"
      ) {
         val moduleInterfaceSpec = TypeSpec.Companion.interfaceBuilder(outputFileName)
            .addAnnotation(Module::class)
            .addAnnotation(contributesToAnnotation)
            .addFunction(bindsServiceFunction)
            .build()

         addType(moduleInterfaceSpec)
         addType(fromBackstackProviderModule)
      }

      return listOf(
         createGeneratedFile(codeGenDir, packageName, outputFileName, content)
      )
   }

   override fun isApplicable(context: AnvilContext): Boolean = true
}

@OptIn(ExperimentalAnvilApi::class)
fun TypeReference.isScopedService(): Boolean {
   val classReference = this.asClassReference()

   return classReference.fqName.asString() == "si.inova.androidarchitectureplayground.screens.ScopedService" ||
      classReference.directSuperTypeReferences().any { it.isScopedService() }
}

@OptIn(ExperimentalAnvilApi::class)
fun ClassReference.Psi.isScopedService(): Boolean {
   return directSuperTypeReferences().any { it.isScopedService() }
}
