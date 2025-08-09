plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-ksp")
}

dependencies {
    implementation(libs.jsoup)

    api(projects.feature.news.domain.api)

    implementation(projects.core.logger.appLogger)
    implementation(projects.core.database)
    implementation(projects.core.commonJvm)
    implementation(projects.core.coroutines)

}
