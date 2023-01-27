import util.commonAndroid
import util.commonKotlinOptions

plugins {
   id("org.jetbrains.kotlin.android")
}

commonAndroid {
   compileSdk = 33

   compileOptions {
      sourceCompatibility = JavaVersion.VERSION_1_8
      targetCompatibility = JavaVersion.VERSION_1_8
   }

   commonKotlinOptions {
      jvmTarget = "1.8"
   }

   defaultConfig {
      testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
   }

   packagingOptions {
      resources {
         excludes += "/META-INF/{AL2.0,LGPL2.1}"
      }
   }
}
