plugins {
    id("convention.android-library")
    id("convention.compose")
    id("n7.plugins.kotlin-ksp")
}

dependencies {
    implementation(libs.workManager)
    implementation(libs.composePaging)
    implementation(libs.webkit)

    implementation(projects.core.commonAndroid)
    implementation(projects.core.dagger)
    implementation(projects.core.coroutines)
    implementation(projects.core.logger.appLogger)
    implementation(projects.core.navigator)
    implementation(projects.core.database)
    implementation(projects.core.appPreference)

    implementation(projects.feature.news.domain.api)

    ksp(libs.daggerAnnotation)
}
