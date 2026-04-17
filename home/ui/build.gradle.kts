plugins {
   androidLibraryModule
   di
   compose
   navigation
   unmock
   showkase
}

android {
   namespace = "si.inova.androidarchitectureplayground.home"

   androidResources.enable = true
}

dependencies {
   api(projects.home.api)

   implementation(projects.commonCompose)
   implementation(projects.login.api)
   implementation(projects.post.api)
   implementation(projects.user.api)
   implementation(libs.androidx.compose.material3.sizeClasses)
   implementation(libs.kotlinova.core)
   implementation(libs.kotlinova.navigation)
   implementation(libs.kotlinova.navigation.deeplink)
}
