plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-ksp")
}

dependencies {
    api(projects.feature.items.domain.api)

    implementation(projects.core.commonJvm)
    implementation(projects.core.coroutines)
    implementation(projects.core.logger.appLogger)

    implementation(libs.room.ktx)

    ksp(libs.room.compiler)
}
