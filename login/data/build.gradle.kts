plugins {
   androidLibraryModule
   di
}

dependencies {
   api(projects.login.api)
   api(libs.dispatch)
   api(libs.kotlin.coroutines)

   implementation(libs.androidx.datastore)
   implementation(libs.androidx.datastore.preferences)

   testImplementation(testFixtures(projects.common))
}
