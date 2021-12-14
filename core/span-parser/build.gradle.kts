plugins {
    androidLibrary()
    kotlinAndroid()
    kotlinKapt()
}

dependencies {
    testImplementation(Lib.Test.testTruth2)
    testImplementation(Lib.Test.mockitokotlin)

    implementation(project(Module.Core.dagger))
    kapt(Lib.daggerAnnotation)
}