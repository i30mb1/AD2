plugins {
    androidLibrary()
    kotlinAndroid()
    kotlinKapt()
}

dependencies {
    implementation(Lib.room)
    implementation(Lib.roomKtx)

    kapt(Lib.roomAnnotation)
}