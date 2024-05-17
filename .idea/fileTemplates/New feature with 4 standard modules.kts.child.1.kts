plugins {
   pureKotlinModule
}

dependencies {
    api(projects.${NAME}.api)
    
    testImplementation(projects.common.test)    
}
