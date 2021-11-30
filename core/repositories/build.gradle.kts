plugins {
    androidLibrary()
    kotlinAndroid()
    kotlinKapt()
}


dependencies {
    implementation(project(Module.Core.database))
    implementation(project(Module.Core.dagger))

    kapt(Lib.daggerAnnotation)
}