plugins {
   androidLibraryModule
   compose
   di
}

android {
    namespace = "si.inova.androidarchitectureplayground.${NAME}.ui"
    
    buildFeatures {
        androidResources = true
    }
}

dependencies {
    api(projects.${NAME}.api)
    
    testImplementation(testFixtures(projects.common))    
}
