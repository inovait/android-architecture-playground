plugins {
   pureKotlinModule
   di
   moshi
   sqldelight
}

sqldelight {
   databases {
      create("Database") {
         packageName.set("si.inova.androidarchitectureplayground.post.sqldelight.generated")
         schemaOutputDirectory.set(file("src/main/sqldelight/databases"))
      }
   }
}

dependencies {
   api(projects.common)
   api(projects.commonRetrofit)
   api(projects.post.api)
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
