plugins {
   androidLibraryModule
   di
}

android {
    namespace = "si.inova.androidarchitectureplayground.${NAME}"
    
    androidResources.enable = true
}

dependencies {
    testImplementation(testFixtures(projects.common))    
}
