plugins {
    id("convention.android-library")
    id("convention.kotlin-serialization")
    id("convention.compose")
    id("n7.plugins.kotlin-ksp")
}

android {
    // the library convention turns the debug variant off for modules that are not ui/impl/demo, unit tests run on release
    testBuildType = "release"
}

dependencies {
    implementation(projects.core.mediaPlayer)
    implementation(projects.core.commonAndroid)
    implementation(projects.core.dagger)
    implementation(projects.core.coroutines)
    implementation(projects.core.repositories)
    implementation(projects.core.logger.appLogger)
    implementation(projects.core.navigator)

    ksp(libs.daggerAnnotation)

    testImplementation(libs.test.junit)
}
