plugins {
    androidLibrary()
    kotlinAndroid()
}

dependencies {
    api(Lib.fragmentKtx)
    api(Lib.activityKtx)
    api(Lib.coreKtx)
    api(Lib.appCompat)
}