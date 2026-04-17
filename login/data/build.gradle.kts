plugins {
   androidLibraryModule
   di
}

dependencies {
   api(projects.login.api)

   implementation(libs.androidx.datastore)
   implementation(libs.androidx.datastore.preferences)
   implementation(libs.dispatch)
   implementation(libs.kotlin.coroutines)

   testImplementation(testFixtures(projects.common))
}
