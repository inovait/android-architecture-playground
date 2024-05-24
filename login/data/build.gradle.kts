plugins {
   androidLibraryModule
   di
   compose
}

dependencies {
   api(projects.common)
   api(projects.login.api)
   api(libs.dispatch)
   api(libs.kotlinova.navigation)

   implementation(projects.commonNavigation)

   implementation(libs.androidx.datastore)
   implementation(libs.androidx.datastore.preferences)
   implementation(libs.kotlin.coroutines)

   testImplementation(projects.common.test)
}
