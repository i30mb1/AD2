plugins {
    androidLibrary()
}

dependencies {
    api(Lib.dagger)

    api(project(Module.Core.android))
}