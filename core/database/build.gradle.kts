plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-kapt")
}

dependencies {
    api(libs.room.ktx)

    implementation(libs.workManager)
    implementation(libs.moshi)
    implementation(libs.room.paging)

    implementation(project(Module.Core.android))
    implementation(project(Module.Core.coroutines))
    implementation(project(Module.Core.dagger))
    implementation(project(Module.Core.logger))

    testImplementation(libs.bundles.test)

    kapt(libs.room.compiler)
    kapt(libs.moshiCodegen)
    kapt(libs.daggerAnnotation)
}
