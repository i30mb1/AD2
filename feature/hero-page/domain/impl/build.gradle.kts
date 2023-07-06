plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-kapt")
}

dependencies {
    api(project(Module.Feature.HeroPage.api))

    implementation(project(Module.Core.database))
    implementation(project(Module.Core.common))

    implementation(libs.coroutines)
    implementation(libs.moshi)

    kapt(libs.moshiCodegen)
}