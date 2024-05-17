plugins {
   pureKotlinModule
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

   implementation(projects.common)
   implementation(projects.commonRetrofit)
   implementation(libs.dispatch)

   testImplementation(projects.common.test)
   testImplementation(projects.commonRetrofit.test)
}
