plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-kapt")
}

dependencies {
    implementation(project(Module.Feature.Items.impl))

    implementation(project(Module.Core.database))
    implementation(project(Module.Core.common))
    implementation(project(Module.Core.coroutines))
    implementation(project(Module.Core.dagger))
    implementation(project(Module.Core.logger))

    implementation(libs.moshi)

    kapt(libs.daggerAnnotation)
}
