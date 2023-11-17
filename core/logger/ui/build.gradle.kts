plugins {
    id("convention.android-library")
    id("convention.compose")
}

dependencies {
    implementation(project(Module.Core.logger))
    implementation(project(Module.Core.common))
    implementation(project(Module.Core.ui))
}
