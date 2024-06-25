plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-kapt")
}

dependencies {
    api(projects.feature.games.mix.domain.api)

    implementation(projects.core.logger.appLogger)
    implementation(projects.core.database)
    implementation(projects.core.commonJvm)
    implementation(projects.core.coroutines)

    implementation(libs.coroutines)

}
