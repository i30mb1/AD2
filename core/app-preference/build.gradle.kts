plugins {
    androidLibrary()
    kotlinKapt()
}

dependencies {
    implementation(Lib.dataStorePref)

    implementation(project(Module.Core.dagger))
    implementation(project(Module.Core.android))

    kapt(Lib.daggerAnnotation)
}