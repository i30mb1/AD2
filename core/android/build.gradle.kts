plugins {
    androidLibrary()
    kotlinKapt()
    kotlinAndroid()
}

dependencies {
    api(Lib.fragmentKtx)
    api(Lib.activityKtx)
    api(Lib.coreKtx)
//    api(Lib.appCompat)
//    implementation(Lib.dagger)
//    kapt(Lib.daggerAnnotation)
}