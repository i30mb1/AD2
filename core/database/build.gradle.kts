plugins {
    id("convention.android-library")
    kotlin("kapt")
}

dependencies {
    api(libs.room)
    api(libs.roomKtx)

    implementation(libs.workManager)
    implementation(libs.moshi)
    implementation(libs.roomPaging)

    implementation(project(Module.Core.android))
    implementation(project(Module.Core.coroutines))
    implementation(project(Module.Core.dagger))
    implementation(project(Module.Core.logger))

    testImplementation(libs.bundles.test)

    kapt(libs.roomAnnotation)
    kapt(libs.moshiCodegen)
    kapt(libs.daggerAnnotation)
}