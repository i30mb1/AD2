plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-ksp")
}

dependencies {
    implementation(projects.feature.games.mix.domain.impl)

    implementation(projects.core.database)
    implementation(projects.core.commonJvm)
    implementation(projects.core.coroutines)
    implementation(projects.core.dagger)
    implementation(projects.core.logger.appLogger)

    ksp(libs.daggerAnnotation)
}
