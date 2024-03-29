plugins {
   androidLibraryModule
   compose
}

android {
    namespace = "si.inova.androidarchitectureplayground.${NAME}.ui"
    
    buildFeatures {
        androidResources = true
    }
}

dependencies {
    api(projects.${NAME}.api)
}
