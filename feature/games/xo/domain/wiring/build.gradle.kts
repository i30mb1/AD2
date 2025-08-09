plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-ksp")
}

dependencies {
    api(projects.feature.games.xo.domain.impl)

    implementation(projects.core.commonJvm)
    implementation(projects.core.coroutines)
    implementation(projects.core.logger.appLogger)
    implementation(projects.core.dagger)

    ksp(libs.daggerAnnotation)
}
