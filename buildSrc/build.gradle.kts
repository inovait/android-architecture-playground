plugins {
   `kotlin-dsl`
   alias(libs.plugins.detekt)
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

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
   reports {
      sarif.required.set(true)
   }
}

dependencies {
   implementation(libs.androidGradleCacheFix)
   implementation(libs.android.agp)
   implementation(libs.anvil)
   implementation(libs.composeGuard)
   implementation(libs.detekt.plugin)
   implementation(libs.dependencyAnalysis)
   implementation(libs.keeperPlugin)
   implementation(libs.kotlin.plugin)
   implementation(libs.kotlinova.gradle)
   implementation(libs.moduleGraphAssert)
   implementation(libs.moshi.ir)
   implementation(libs.orgJson)
   implementation(libs.ksp)
   implementation(libs.sqldelight.gradle)
   implementation(libs.tomlj)

   // Workaround to have libs accessible (from https://github.com/gradle/gradle/issues/15383)
   compileOnly(files(libs.javaClass.superclass.protectionDomain.codeSource.location))

   detektPlugins(libs.detekt.formatting)
   detektPlugins(libs.detekt.compilerWarnings)
   detektPlugins(libs.detekt.compose)
}

tasks.register("pre-commit-hook", Copy::class) {
   from("$rootDir/../config/hooks/")
   into("$rootDir/../.git/hooks")
}

afterEvaluate {
   tasks.getByName("jar").dependsOn("pre-commit-hook")
}
