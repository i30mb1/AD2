plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-kapt")
}


dependencies {
    api(project(Module.Core.database))
    implementation(project(Module.Core.dagger))
    implementation(project(Module.Core.android))

    implementation(libs.moshi)

    kapt(libs.moshiCodegen)
    kapt(libs.daggerAnnotation)
}