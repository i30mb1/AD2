plugins {
    androidLibrary()
    kotlinKapt()
}

dependencies {
    implementation(Lib.exoPlayerCore)
    implementation(Lib.exoPlayerUi)
    implementation(Lib.exoPlayerMediaSession)
    implementation(Lib.exoPlayerHls)

    implementation(project(Module.Core.dagger))
    kapt(Lib.daggerAnnotation)
}