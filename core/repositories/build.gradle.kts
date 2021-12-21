plugins {
    androidLibrary()
    kotlinKapt()
}


dependencies {
    api(project(Module.Core.database))
    implementation(project(Module.Core.dagger))
    implementation(project(Module.Core.android))

    kapt(Lib.daggerAnnotation)
}