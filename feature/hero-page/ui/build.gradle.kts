plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-kapt")
}

dependencies {
    implementation(libs.workManager)
    implementation(libs.jsoup)
    implementation(libs.ticker)

    implementation(projects.core.mediaPlayer)
    implementation(projects.core.commonAndroid)
    implementation(projects.core.dagger)
    implementation(projects.core.coroutines)
    implementation(projects.core.repositories)
    implementation(projects.core.logger.appLogger)
    implementation(projects.core.navigator)
    implementation(projects.core.spanParser)
    implementation(projects.core.mediaPlayer)

    implementation(projects.feature.heroes.domain.api)
    implementation(projects.feature.heroPage.domain.api)

    api(libs.moshi)

    kapt(libs.moshiCodegen)
    kapt(libs.daggerAnnotation)
}
