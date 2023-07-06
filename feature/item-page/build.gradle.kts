plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-kapt")
}

dependencies {
    implementation(libs.jsoup)

    implementation(project(Module.Core.mediaPlayer))
    implementation(project(Module.Core.android))
    implementation(project(Module.Core.dagger))
    implementation(project(Module.Core.coroutines))
    implementation(project(Module.Core.repositories))
    implementation(project(Module.Core.logger))
    implementation(project(Module.Core.navigator))
    implementation(project(Module.Core.spanParser))
    implementation(project(Module.Core.mediaPlayer))

    api(libs.moshi)

    kapt(libs.moshiCodegen)
    kapt(libs.daggerAnnotation)
}