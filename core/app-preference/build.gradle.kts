plugins {
    androidLibrary()
    kapt()
}

dependencies {
    implementation(Lib.dataStorePref)

    implementation(project(Module.Core.dagger))

    kapt(Lib.daggerAnnotation)
}