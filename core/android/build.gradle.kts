plugins {
    androidLibrary()
    kotlinAndroid()
}

dependencies {
    api(Lib.fragmentKtx)
    api(Lib.activityKtx)
    api(Lib.coreKtx)
    api(Lib.appCompat)
    api(Lib.recyclerView)
    api(Lib.paging3)
    api(Lib.material)
    api(Lib.constraintLayout)

    implementation(Lib.coil)
}