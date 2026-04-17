plugins {
   pureKotlinModule
   di
   serialization
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

   implementation(projects.commonPaging)
   implementation(projects.commonRetrofit)

   implementation(libs.androidx.paging.common)
   implementation(libs.dispatch)
   implementation(libs.kotlin.coroutines)
   implementation(libs.kotlinova.core)
   implementation(libs.kotlinova.retrofit)
   implementation(libs.retrofit)
   implementation(libs.sqldelight.paging)

   testImplementation(testFixtures(projects.common))
   testImplementation(testFixtures(projects.commonRetrofit))
   testImplementation(libs.androidx.paging.testing)
   testImplementation(libs.kotlinova.core.test)
   testImplementation(libs.kotlinova.retrofit.test)
   testImplementation(libs.okhttp.mockWebServer)
}
