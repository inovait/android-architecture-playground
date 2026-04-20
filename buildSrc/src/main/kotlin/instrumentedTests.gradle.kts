val androidTestJar by tasks.registering(Jar::class) {
   archiveClassifier.set("androidTest")

   from(fileTree("src/androidTest/resources"))

   exclude("caches-jvm/**")
   exclude("**/*.bin")
}

val androidTestClasses by configurations.creating {
   isCanBeResolved = false
   isCanBeConsumed = true
   extendsFrom(configurations.getByName("androidTestImplementation"))
}

afterEvaluate {
   val testCompilation = tasks.named("compileDebugAndroidTestKotlin")
   androidTestJar.configure {
      from(testCompilation.map { it.outputs.files })
      dependsOn(testCompilation)
   }
}

artifacts {
   add(androidTestClasses.name, androidTestJar)
}

configure<CustomBuildConfiguration> {
   enableEmulatorTests.set(true)
}
