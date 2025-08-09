plugins {
    id("convention.kotlin-jvm")
    id("n7.plugins.kotlin-ksp")
}

dependencies {
    implementation(projects.core.dagger)
    implementation(projects.core.logger.appLogger)
    implementation(projects.core.commonJvm)

    api(libs.retrofit)
    api(libs.retrofitInterceptor)
    api(libs.retrofitScalars)
    api(libs.retrofitSerialization)

    ksp(libs.daggerAnnotation)
}
