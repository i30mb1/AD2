plugins {
    androidLibrary()
    kotlinAndroid()
}

dependencies {
    implementation(project(Module.Core.dagger))
    implementation(project(Module.Core.coroutines))
}