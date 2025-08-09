plugins {
    id("convention.android-library")
    id("convention.kotlin-serialization")
    id("n7.plugins.kotlin-ksp")
}

dependencies {
    api(projects.feature.heroes.domain.api)

    implementation(projects.core.commonJvm)
    implementation(projects.core.coroutines)
    implementation(projects.core.logger.appLogger)

    implementation(libs.kotlinSerialization)
    implementation(libs.room.ktx)

    ksp(libs.room.compiler)
}
