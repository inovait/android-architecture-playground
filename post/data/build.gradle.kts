plugins {
   pureKotlinModule
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
   api(projects.post.api)

   implementation(projects.common)
   implementation(projects.commonRetrofit)
   implementation(libs.dispatch)

   testImplementation(projects.commonRetrofit.test)
}
