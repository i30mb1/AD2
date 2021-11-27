plugins {
    androidLibrary()
//    kotlinKapt()
    kotlinAndroid()
}

dependencies {
    implementation(project(Module.Core.android))
//    implementation(Lib.dagger)
//    kapt(Lib.daggerAnnotation)
}
