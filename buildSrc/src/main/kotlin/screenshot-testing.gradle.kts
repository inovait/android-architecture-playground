import org.gradle.accessors.dm.LibrariesForLibs
import util.commonAndroid

val libs = the<LibrariesForLibs>()

// We have to apply shot plugin the old way. If we used plugins {} block, it is applied before Android plugin
apply(plugin = "shot")

commonAndroid {
   defaultConfig {
      testApplicationId = "si.inova.kotlinova.core.test"
      testInstrumentationRunner = "com.karumi.shot.ShotTestRunner"
   }
}
