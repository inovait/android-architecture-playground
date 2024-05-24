plugins {
   pureKotlinModule
   di
   moshi
   sqldelight
}

sqldelight {
   databases {
      create("Database") {
         packageName.set("si.inova.androidarchitectureplayground.user.sqldelight.generated")
         schemaOutputDirectory.set(file("src/main/sqldelight/databases"))
      }
   }
}

dependencies {
   api(projects.user.api)
   api(projects.common)
   api(projects.commonRetrofit)

   api(libs.kotlin.coroutines)
   api(libs.kotlinova.core)
   api(libs.retrofit)
   implementation(libs.dispatch)

   testImplementation(projects.common.test)
   testImplementation(projects.commonRetrofit.test)
   testImplementation(libs.kotlinova.core.test)
   testImplementation(libs.kotlinova.retrofit.test)
   testImplementation(libs.okhttp)
   testImplementation(libs.okhttp.mockWebServer)
}
