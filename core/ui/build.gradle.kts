plugins {
    androidLibrary()
    kotlinAndroid()
}

dependencies {
    api(Lib.appCompat)
    api(Lib.splashScreen)
    api(Lib.material)

    implementation(project(Module.Core.ktx))
}