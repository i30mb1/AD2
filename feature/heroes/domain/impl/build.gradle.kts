plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-kapt")
}

dependencies {
    api(project(Module.Feature.Heroes.api))

    implementation(project(Module.Core.database))
    implementation(project(Module.Core.common))
    implementation(project(Module.Core.coroutines))

    implementation(libs.moshi)
    
    kapt(libs.moshiCodegen)
}