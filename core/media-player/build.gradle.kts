plugins {
    androidLibrary()
    kotlinKapt()
}

dependencies {
    implementation(Lib.exoPlayerCore)

    implementation(project(Module.Core.dagger))
    implementation(project(Module.Core.android))
    kapt(Lib.daggerAnnotation)
}