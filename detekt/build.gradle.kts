plugins {
   pureKotlinModule
}

dependencyAnalysis {
   issues {
      onUsedTransitiveDependencies {
         // We don't really want to include entire kotlin here.
         // Detekt can manage its own dependencies.
         exclude("org.jetbrains.kotlin:kotlin-compiler")
         exclude("dev.detekt:detekt-kotlin-analysis-api")
      }
   }
}


dependencies {
   compileOnly(libs.detekt.api)

   testImplementation(libs.detekt.api)
   testImplementation(libs.detekt.test)
}
