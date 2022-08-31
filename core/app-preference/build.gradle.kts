plugins {
    androidLibrary()
    kapt()
}

dependencies {
    implementation(libs.dataStorePref)

    implementation(project(Module.Core.dagger))

    kapt(libs.daggerAnnotation)
}