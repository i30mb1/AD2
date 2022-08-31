plugins {
    kotlinLibrary()
    kapt()
}

dependencies {
    implementation(project(Module.Core.dagger))
    kapt(libs.daggerAnnotation)

    api(libs.coroutines)
}