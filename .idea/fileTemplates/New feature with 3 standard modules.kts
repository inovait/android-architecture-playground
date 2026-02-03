plugins {
   androidLibraryModule
   compose
   di
}

android {
    namespace = "si.inova.androidarchitectureplayground.${NAME}.ui"
    
    androidResources.enable = true
}

dependencies {
    api(projects.${NAME}.api)
    
    testImplementation(testFixtures(projects.common))    
}
