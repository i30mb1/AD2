plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-kapt")
}

dependencies {
    api(project(Module.Feature.Items.api))

    implementation(project(Module.Core.common))
    implementation(project(Module.Core.coroutines))
    implementation(project(Module.Core.logger))

    implementation(libs.moshi)
    implementation(libs.roomKtx)

    kapt(libs.moshiCodegen)
    kapt(libs.roomAnnotation)
}
