plugins {
    androidLibrary()
    kotlinAndroid()
    kotlinKapt()
}


dependencies {
    api(project(Module.Core.database))
    implementation(project(Module.Core.dagger))

    kapt(Lib.daggerAnnotation)
}