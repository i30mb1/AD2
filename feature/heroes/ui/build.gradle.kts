plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-kapt")
}

dependencies {
    implementation(libs.moshi)

    implementation(project(Module.Core.android))
    implementation(project(Module.Core.dagger))
    implementation(project(Module.Core.coroutines))
    implementation(project(Module.Core.repositories))
    implementation(project(Module.Core.logger))
    implementation(project(Module.Core.navigator))

    implementation(project(Module.Feature.Heroes.api))

    kapt(libs.daggerAnnotation)

    androidTestImplementation(libs.fragmentTesting)
    androidTestImplementation(libs.testJunit)
    androidTestImplementation(libs.mockk)
    androidTestImplementation(libs.espresso)
}
