plugins {
    id("convention.kotlin-jvm")
    id("n7.plugins.kotlin-kapt")
}

dependencies {
    api(libs.coroutines)
    implementation(project(Module.Core.dagger))
    api(libs.bundles.test)

    kapt(libs.daggerAnnotation)
}
