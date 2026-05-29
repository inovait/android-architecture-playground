plugins {
   androidLibraryModule
   compose
   alias(libs.plugins.paparazzi)
}

android {
   namespace = "si.inova.androidarchitectureplayground.screenshottests"

   androidResources.enable = true

   testOptions {
      unitTests.all {
         it.useJUnit()

         val numSplits = 2 // How many TestsX classes are there
         it.maxParallelForks = minOf(Runtime.getRuntime().availableProcessors(), numSplits)
         it.systemProperty("numSplits", numSplits)
      }
   }
}

dependencyAnalysis {
   issues {
      onModuleStructure {
         // False positive
         severity("ignore")
      }
   }
}

dependencies {
   implementation(projects.app) {
      // If your app has multiple flavors, this is how you define them:
      //      attributes {
      //         attribute(
      //            ProductFlavorAttr.of("app"),
      //            objects.named(ProductFlavorAttr::class.java, "develop")
      //         )
      //      }
   }
   testImplementation(libs.junit4)
   testImplementation(libs.junit4.parameterinjector)
   testImplementation(libs.showkase)
}
