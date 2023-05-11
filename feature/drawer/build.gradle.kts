plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-kapt")
}

dependencies {
    implementation(project(Module.Core.android))
    implementation(project(Module.Core.dagger))
    implementation(project(Module.Core.coroutines))
    implementation(project(Module.Core.logger))
    implementation(project(Module.Core.ui))
    implementation(project(Module.Core.provider))
    implementation(project(Module.Core.appPreference))
    implementation(project(Module.Core.retrofit))

    api(libs.moshi)

    kapt(libs.moshiCodegen)
    kapt(libs.daggerAnnotation)

    testImplementation(libs.bundles.test)
//    testImplementation(testFixtures(project(Module.Core.appPreference)))
    testImplementation(testFixtures(project(Module.Core.common)))
    testImplementation(testFixtures(project(Module.Core.logger)))
}
