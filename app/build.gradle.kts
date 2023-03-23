// https://youtrack.jetbrains.com/issue/KTIJ-19369
// AGP 7.4.0 has a bug where it marks most things as incubating
@file:Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")

import com.android.build.api.variant.BuildConfigField

plugins {
   androidAppModule
   compose
   id("kotlin-parcelize")
}

android {
   namespace = "si.inova.androidarchitectureplayground"

   defaultConfig {
      applicationId = "si.inova.androidarchitectureplayground"
      targetSdk = 33
      versionCode = 1
      versionName = "1.0"

      androidComponents {
         onVariants {
            it.buildConfigFields.put("GIT_HASH", gitVersionProvider.flatMap { task ->
               task.gitVersionOutputFile.map { file ->
                  val gitHash = file.asFile.readText(Charsets.UTF_8)

                  BuildConfigField(
                     "String",
                     "\"$gitHash\"",
                     "Git Version"
                  )
               }
            })
         }
      }
   }

   buildTypes {
      getByName("release") {
         isMinifyEnabled = true
         proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
         )
      }

   }
}

dependencies {
   implementation(projects.commonAndroid)
   implementation(projects.libmodule)
   implementation(projects.login)
   implementation(projects.masterdetail)
   implementation(projects.commonNavigation)
   implementation(projects.commonRetrofit.android)
   implementation(projects.products.ui)
   implementation(projects.screenC)
   implementation(projects.pureKotlinModule)
   implementation(projects.commonCompose)

   implementation(libs.androidx.activity.compose)
   implementation(libs.androidx.core)
   implementation(libs.androidx.core.splashscreen)
   implementation(libs.androidx.lifecycle.runtime)
   implementation(libs.androidx.lifecycle.viewModel)
   implementation(libs.androidx.lifecycle.viewModel.compose)
   implementation(libs.dispatch)
   implementation(libs.retrofit.moshi)
   implementation(libs.simpleStack)

   testImplementation(projects.commonAndroid.test)
   testImplementation(libs.junit4)
}

abstract class GitVersionTask : DefaultTask() {
   @get:OutputFile
   abstract val gitVersionOutputFile: RegularFileProperty

   @TaskAction
   fun taskAction() {
      val gitProcess = ProcessBuilder("git", "rev-parse", "--short", "HEAD").start()
      val error = gitProcess.errorStream.readBytes().decodeToString()
      if (error.isNotBlank()) {
         throw IllegalStateException("Git error : $error")
      }

      val gitVersion = gitProcess.inputStream.readBytes().decodeToString().trim()

      gitVersionOutputFile.get().asFile.writeText(gitVersion)
   }
}

val gitVersionProvider = tasks.register<GitVersionTask>("gitVersionProvider") {
   val targetFile = File(project.buildDir, "intermediates/gitVersionProvider/output")

   targetFile.also {
      it.parentFile.mkdirs()
      gitVersionOutputFile.set(it)
   }
   outputs.upToDateWhen { false }
}
