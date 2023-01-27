// https://youtrack.jetbrains.com/issue/KTIJ-19369
// AGP 7.4.0 has a bug where it marks most things as incubating
@file:Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")

plugins {
   id("com.android.application")
   commonAndroid
   compose
}

android {
   namespace = "si.inova.androidarchitectureplayground"

   defaultConfig {
      applicationId = "si.inova.androidarchitectureplayground"
      minSdk = 24
      targetSdk = 33
      versionCode = 1
      versionName = "1.0"

      androidComponents {
         onVariants {
            it.buildConfigFields.put("GIT_HASH", gitVersionProvider.map {  task ->
               com.android.build.api.variant.BuildConfigField(
                  "String",
                  "\"${task.gitVersionOutputFile.get().asFile.readText(Charsets.UTF_8)}\"",
                  "Git Version"
               )
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
   implementation(libs.androidx.activity.compose)
   implementation(libs.androidx.core)
   implementation(libs.androidx.lifecycle.runtime)

   testImplementation(libs.junit4)
}

abstract class GitVersionTask : DefaultTask() {
   @get:OutputFile
   abstract val gitVersionOutputFile: RegularFileProperty

   @ExperimentalStdlibApi
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
   val targetFile = if (File("/tmp").exists()) {
      File("/tmp", "gitVersionProvider-${System.currentTimeMillis()}")
   } else {
      File(project.buildDir, "intermediates/gitVersionProvider/output")
   }

   targetFile.also {
      it.parentFile.mkdirs()
      gitVersionOutputFile.set(it)
   }
   outputs.upToDateWhen { false }
}
