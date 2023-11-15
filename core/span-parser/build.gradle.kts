plugins {
    id("convention.android-library")
    id("convention.jacoco")
    id("n7.plugins.kotlin-kapt")
}

android {
//    kotlin {
//        explicitApi()
//    }
}

dependencies {
    implementation(project(Module.Core.dagger))
    implementation(project(Module.Core.android))

    testImplementation(libs.testTruth)
    testImplementation(libs.mockitokotlin)
    testImplementation(testFixtures(project(Module.Core.common)))

    kapt(libs.daggerAnnotation)
}
