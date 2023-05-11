plugins {
    id("convention.kotlin-jvm")
    id("n7.plugins.kotlin-kapt")
}

dependencies {
    api(libs.coroutines)
    implementation(project(Module.Core.dagger))
    implementation(libs.bundles.test)

    kapt(libs.daggerAnnotation)
}