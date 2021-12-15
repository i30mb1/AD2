plugins {
    androidLibrary()
    kotlinKapt()
}

dependencies {
    implementation(Lib.dataStorePref)

    implementation(project(Module.Core.dagger))

    kapt(Lib.daggerAnnotation)
}