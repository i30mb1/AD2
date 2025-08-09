plugins {
    id("convention.android-library")
    id("convention.compose")
    id("n7.plugins.kotlin-ksp")
}

dependencies {
    implementation(libs.palette)

    implementation(projects.core.commonAndroid)
    implementation(projects.core.dagger)
    implementation(projects.core.coroutines)
    implementation(projects.core.logger.appLogger)
    implementation(projects.core.navigator)
    implementation(projects.core.repositories)
    implementation(projects.core.secret)

    implementation(projects.feature.games.mix.domain.api)
    implementation(projects.feature.heroes.domain.api)

    ksp(libs.daggerAnnotation)
}
