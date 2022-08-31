plugins {
    id("convention.android-library")
    kotlin("kapt")
}

dependencies {
    implementation(libs.jsoup)
    implementation(libs.exoPlayerCore)

    implementation(project(Module.Core.android))
    implementation(project(Module.Core.dagger))
    implementation(project(Module.Core.coroutines))
    implementation(project(Module.Core.repositories))
    implementation(project(Module.Core.logger))
    implementation(project(Module.Core.provider))
    implementation(project(Module.Core.spanParser))
    implementation(project(Module.Core.mediaPlayer))

    api(libs.moshi)

    kapt(libs.moshiCodegen)
    kapt(libs.daggerAnnotation)
}