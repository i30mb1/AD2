plugins {
    id("convention.kotlin-jvm")
    kotlin("kapt")
}

dependencies {
    implementation(project(Module.Core.dagger))
    implementation(project(Module.Core.logger))
    implementation(project(Module.Core.common))

    api(libs.retrofit)
    api(libs.retrofitMoshiConverter)
    api(libs.retrofitInterceptor)
    api(libs.retrofitScalars)

    kapt(libs.daggerAnnotation)
}