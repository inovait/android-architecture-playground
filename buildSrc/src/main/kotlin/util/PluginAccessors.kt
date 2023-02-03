import org.gradle.plugin.use.PluginDependenciesSpec
import org.gradle.plugin.use.PluginDependencySpec

inline val PluginDependenciesSpec.commonAndroid: PluginDependencySpec
   get() = id("android-module-commons")

inline val PluginDependenciesSpec.compose: PluginDependencySpec
   get() = id("compose")

inline val PluginDependenciesSpec.androidAppModule: PluginDependencySpec
   get() = id("app-module")

inline val PluginDependenciesSpec.androidLibraryModule: PluginDependencySpec
   get() = id("library-android-module")
