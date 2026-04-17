plugins {
   pureKotlinModule
   di
   serialization
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
   api(projects.post.api)

   implementation(projects.common)
   implementation(projects.commonRetrofit)
   implementation(libs.dispatch)
   implementation(libs.kotlin.coroutines)
   implementation(libs.kotlinova.core)
   implementation(libs.retrofit)

   testImplementation(testFixtures(projects.common))
   testImplementation(testFixtures(projects.commonRetrofit))
   testImplementation(libs.kotlinova.core.test)
   testImplementation(libs.kotlinova.retrofit.test)
   testImplementation(libs.okhttp.mockWebServer)
}
