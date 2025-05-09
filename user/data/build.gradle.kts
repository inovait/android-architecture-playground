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
   api(projects.commonRetrofit)

   api(libs.dispatch)
   api(libs.kotlin.coroutines)
   api(libs.kotlinova.core)
   api(libs.retrofit)
   implementation(libs.androidx.paging.common)
   implementation(libs.sqldelight.paging)

   testImplementation(projects.common.test)
   testImplementation(projects.commonRetrofit.test)
   testImplementation(libs.androidx.paging.testing)
   testImplementation(libs.kotlinova.core.test)
   testImplementation(libs.kotlinova.retrofit.test)
   testImplementation(libs.okhttp.mockWebServer)
}
