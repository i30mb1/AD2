plugins {
    androidLibrary()
    kotlinAndroid()
}

dependencies {
    api(Lib.dagger)

    api(project(Module.Core.android))
}