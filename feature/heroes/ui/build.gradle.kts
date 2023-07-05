plugins {
    id("convention.android-library")
}

dependencies {
    implementation(project(Module.Feature.Heroes.api))
}