plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-kapt")
}

dependencies {
    api(project(Module.Feature.Heroes.api))

    implementation(project(Module.Core.common))
    implementation(project(Module.Core.coroutines))
    implementation(project(Module.Core.logger))

    implementation(libs.moshi)
    implementation(libs.room.ktx)

    kapt(libs.moshiCodegen)
    kapt(libs.room.compiler)
}
