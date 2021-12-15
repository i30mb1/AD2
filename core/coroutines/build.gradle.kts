plugins {
    androidLibrary()
    kotlinKapt()
}

dependencies {
    implementation(project(Module.Core.dagger))
    kapt(Lib.daggerAnnotation)

    api(Lib.coroutines)
    api(Lib.coroutinesLifecycle)
    api(Lib.coroutinesLivedata)
    api(Lib.coroutinesViewmodel)
}