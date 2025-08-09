plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-ksp")
}

dependencies {
    api(projects.feature.heroes.domain.impl)

    implementation(projects.core.commonJvm)
    implementation(projects.core.coroutines)
    implementation(projects.core.dagger)
    implementation(projects.core.logger.appLogger)

    ksp(libs.daggerAnnotation)
}
