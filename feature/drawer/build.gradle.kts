plugins {
    id("convention.android-library")
    id("convention.kotlin-serialization")
    id("convention.compose")
    id("n7.plugins.kotlin-ksp")
}

android {
    testFixtures.enable = true
    // the library convention turns the debug variant off for modules that are not ui/impl/demo, unit tests run on release
    testBuildType = "release"
    buildFeatures {
        dataBinding = false
    }
}

dependencies {
    implementation(projects.core.commonAndroid)
    implementation(projects.core.dagger)
    implementation(projects.core.coroutines)
    implementation(projects.core.logger.appLogger)
    implementation(projects.core.ui)
    implementation(projects.core.navigator)
    implementation(projects.core.appPreference)
    implementation(projects.core.retrofit)
    implementation(projects.feature.heroes.domain.api)
    implementation(projects.feature.items.domain.api)
    implementation(projects.feature.news.domain.api)

    ksp(libs.daggerAnnotation)

    testImplementation(testFixtures(projects.core.appPreference))
    testImplementation(testFixtures(projects.core.commonJvm))
    testImplementation(testFixtures(projects.core.logger.appLogger))
    testImplementation(libs.test.junit)
    testImplementation(libs.test.truth)
}
