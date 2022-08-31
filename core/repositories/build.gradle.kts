plugins {
    androidLibrary()
    kapt()
}


dependencies {
    api(project(Module.Core.database))
    implementation(project(Module.Core.dagger))
    implementation(project(Module.Core.android))

    kapt(libs.daggerAnnotation)
}