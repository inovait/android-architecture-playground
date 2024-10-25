import com.android.build.api.variant.BuildConfigField
import com.android.build.api.variant.VariantOutputConfiguration
import com.squareup.anvil.plugin.AnvilExtension
import org.gradle.accessors.dm.LibrariesForLibs
import tasks.setupTooManyKotlinFilesTask

val libs = the<LibrariesForLibs>()

plugins {
   id("com.android.application")
   id("android-module-commons")
   id("kotlin-kapt")
   id("kotlinova")
   id("com.jraska.module.graph.assertion")
   id("com.squareup.anvil")
}

afterEvaluate {
   if (pluginManager.hasPlugin("com.squareup.anvil")) {
      configure<AnvilExtension> {
         syncGeneratedSources.set(true)
      }
   }
}

kotlinova {
   tomlVersionBump {
      versionReportFiles.set(
         fileTree(rootDir).apply {
            include("**/build/dependencyUpdates/versions.json")
         }
      )

      tomlFile.set(File(rootDir, "config/libs.toml"))
   }
}

moduleGraphAssert {
   maxHeight = 6
   restricted = arrayOf(
      ":common-navigation -X> .*",

      // Prevent all modules but this app module from depending on :data and :ui modules
      ":(?!$name).* -X> .*:data",
      ":(?!$name).* -X> .*:ui",

      // Only allow common modules to depend on other common modules and shared resources
      ":common-.* -X> :(?!common).*",
   )
}

android {
   lint {
      checkDependencies = true
   }

   androidComponents {
      onVariants { variant ->
         val mainOutput =
            variant.outputs.single { it.outputType == VariantOutputConfiguration.OutputType.SINGLE }

         val gitHashProvider = providers.exec {
            commandLine("git", "rev-parse", "--short", "HEAD")
         }.standardOutput.asText.map { it.trim() }

         val baseVersionName = defaultConfig.versionName
         val buildNumberProvider = providers.environmentVariable("BUILD_NUMBER")

         // A bit weird syntax as a workaround for the https://github.com/gradle/gradle/issues/30792
         val appendedVersionName = buildNumberProvider.map { "$baseVersionName-$it" }
            .orElse(gitHashProvider.map { gitHash -> "$baseVersionName-local-$gitHash" })

         variant.buildConfigFields.put(
            "VERSION_NAME",
            appendedVersionName.map {
               BuildConfigField(
                  "String",
                  "\"$it\"",
                  "App version"
               )
            }
         )

         mainOutput.versionName.set(appendedVersionName)
         mainOutput.versionCode.set(buildNumberProvider.orElse("1").map { it.toInt() })
      }
   }
}

dependencies {
   implementation(libs.dagger.runtime)
   kapt(libs.dagger.compiler)
   androidTestImplementation(libs.androidx.test.runner)
}

project.setupTooManyKotlinFilesTask()
