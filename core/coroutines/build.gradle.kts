plugins {
    kotlinLibrary()
    kapt()
}

dependencies {
    implementation(project(Module.Core.dagger))
    kapt(Lib.daggerAnnotation)

    api(Lib.coroutines)
}