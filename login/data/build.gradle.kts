plugins {
   androidLibraryModule
   compose
}

dependencies {
   api(projects.login.api)
   implementation(projects.common)
   implementation(projects.commonNavigation)

   implementation(libs.dispatch)
   implementation(libs.androidx.datastore)
   implementation(libs.androidx.datastore.preferences)

   testImplementation(projects.common.test)
}
