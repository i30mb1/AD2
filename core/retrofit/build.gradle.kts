plugins {
    id("convention.kotlin-jvm")
    id("n7.plugins.kotlin-kapt")
}

dependencies {
    implementation(projects.core.dagger)
    implementation(projects.core.logger.appLogger)
    implementation(projects.core.commonJvm)

    api(libs.retrofit)
    api(libs.retrofitMoshiConverter)
    api(libs.retrofitInterceptor)
    api(libs.retrofitScalars)

    kapt(libs.daggerAnnotation)
}
