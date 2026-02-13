plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-ksp")
    id("convention.kover")
}

android {
//    kotlin {
//        explicitApi()
//    }
}

dependencies {
    implementation(projects.core.dagger)
    implementation(projects.core.commonAndroid)

    testImplementation(libs.test.truth)
    testImplementation(testFixtures(projects.core.commonJvm))

    ksp(libs.daggerAnnotation)
}
