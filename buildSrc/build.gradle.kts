// https://youtrack.jetbrains.com/issue/KTIJ-19369
// AGP 7.4.0 has a bug where it marks most things as incubating
@file:Suppress("DSL_SCOPE_VIOLATION", "UnstableApiUsage")

plugins {
   `kotlin-dsl`
   alias(libs.plugins.detekt)
}

repositories {
   google()
   mavenCentral()
   gradlePluginPortal()
}

detekt {
   config = files("$projectDir/../config/detekt.yml", "$projectDir/../config/detekt-buildSrc.yml")
}

dependencies {
   implementation(libs.android.agp)
   implementation(libs.detekt.plugin)
   implementation(libs.kotlin.plugin)
   implementation(libs.versionsCheckerPlugin)

   // Workaround to have libs accessible (from https://github.com/gradle/gradle/issues/15383)
   compileOnly(files(libs.javaClass.superclass.protectionDomain.codeSource.location))

   detektPlugins(libs.detekt.formatting)
}
