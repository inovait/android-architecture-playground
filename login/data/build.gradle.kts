plugins {
   androidLibraryModule
   di
}

dependencies {
   api(projects.login.api)
   api(libs.dispatch)
   api(libs.kotlin.coroutines)
   api(libs.kotlinova.navigation)

   implementation(projects.commonNavigation)

   implementation(libs.androidx.datastore)
   implementation(libs.androidx.datastore.preferences)

   testImplementation(testFixtures(projects.common))
}
