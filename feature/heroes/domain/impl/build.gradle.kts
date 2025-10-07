plugins {
    id("convention.android-library")
    id("convention.kotlin-serialization")
    id("n7.plugins.kotlin-ksp")
}

android {
    testFixtures.enable = true
}

dependencies {
    api(projects.feature.heroes.domain.api)

    implementation(projects.core.commonJvm)
    implementation(projects.core.coroutines)
    implementation(projects.core.logger.appLogger)

    implementation(libs.kotlinSerialization)
    implementation(libs.coroutines)
    implementation(libs.room.ktx)

    ksp(libs.room.compiler)

    testFixturesImplementation(libs.coroutines)
}
