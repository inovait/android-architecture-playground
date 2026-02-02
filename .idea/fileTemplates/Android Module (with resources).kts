plugins {
   androidLibraryModule
   di
}

android {
    namespace = "si.inova.androidarchitectureplayground.${NAME}"
    
    buildFeatures {
        androidResources = true
    }
}

dependencies {
    testImplementation(testFixtures(projects.common))    
}
