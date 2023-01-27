plugins {
   `kotlin-dsl`
}

repositories {
   google()
   mavenCentral()
}

dependencies {
   implementation(libs.android.agp)
   implementation(libs.kotlin.plugin)

   // Workaround to have libs accessible (from https://github.com/gradle/gradle/issues/15383)
   compileOnly(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}
