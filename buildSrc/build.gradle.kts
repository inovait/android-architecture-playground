// https://youtrack.jetbrains.com/issue/KTIJ-19369
// AGP 7.4.0 has a bug where it marks most things as incubating
@file:Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")

import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
   `kotlin-dsl`
   alias(libs.plugins.detekt)
   alias(libs.plugins.versionsChecker)
}

repositories {
   mavenLocal()
   google()
   mavenCentral()
   gradlePluginPortal()
}

detekt {
   config.setFrom("$projectDir/../config/detekt.yml", "$projectDir/../config/detekt-buildSrc.yml")
}

tasks.withType<DependencyUpdatesTask> {
   gradleReleaseChannel = "current"

   rejectVersionIf {
      candidate.version.contains("alpha", ignoreCase = true) ||
         candidate.version.contains("beta", ignoreCase = true) ||
         candidate.version.contains("RC", ignoreCase = true) ||
         candidate.version.contains("M", ignoreCase = true) ||
         candidate.version.contains("eap", ignoreCase = true) ||
         candidate.version.contains("dev", ignoreCase = true) ||
         candidate.version.contains("pre", ignoreCase = true)
   }

   reportfileName = "versions"
   outputFormatter = "json"
}

dependencies {
   implementation(libs.androidGradleCacheFix)
   implementation(libs.android.agp)
   implementation(libs.anvil)
   implementation(libs.detekt.plugin)
   implementation(libs.kotlin.plugin)
   implementation(libs.kotlinova.gradle)
   implementation(libs.moduleGraphAssert)
   implementation(libs.moshi.ir)
   implementation(libs.orgJson)
   implementation(libs.versionsCheckerPlugin)
   implementation(libs.ksp)
   implementation(libs.shot)
   implementation(libs.tomlj)

   // Workaround to have libs accessible (from https://github.com/gradle/gradle/issues/15383)
   compileOnly(files(libs.javaClass.superclass.protectionDomain.codeSource.location))

   detektPlugins(libs.detekt.formatting)
   detektPlugins(libs.detekt.compilerWarnings)
   detektPlugins(libs.detekt.twitterCompose)
}

tasks.register("pre-commit-hook", Copy::class) {
   from("$rootDir/../config/hooks/")
   into("$rootDir/../.git/hooks")
}

afterEvaluate {
   tasks.getByName("jar").dependsOn("pre-commit-hook")
}
