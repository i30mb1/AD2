plugins {
    androidLibrary()
    kotlinKapt()
}

dependencies {
    testImplementation(Lib.Test.testTruth2)
    testImplementation(Lib.Test.mockitokotlin)

    implementation(project(Module.Core.dagger))
    implementation(project(Module.Core.android))
    kapt(Lib.daggerAnnotation)
}