plugins {
    androidLibrary()
    kotlinAndroid()
}

dependencies {
    api(Lib.fragmentKtx)
    api(Lib.activityKtx)
    api(Lib.coreKtx)
    api(Lib.recyclerView)
    api(Lib.paging3)
    api(Lib.constraintLayout)
    api(project(Module.Core.ui))

    implementation(Lib.coil)
}