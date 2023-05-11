plugins {
   androidLibraryModule
   compose
}

dependencies {
   api(projects.login.api)
   implementation(projects.commonNavigation)

   implementation(libs.dispatch)
   implementation(libs.androidx.datastore)
   implementation(libs.androidx.datastore.preferences)
}
