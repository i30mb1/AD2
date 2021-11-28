plugins {
    androidLibrary()
    kotlinAndroid()
    kotlinKapt()
}

dependencies {
    implementation(Lib.room)
    implementation(Lib.roomKtx)

    implementation(project(Module.Core.database_guides))

    kapt(Lib.roomAnnotation)
}