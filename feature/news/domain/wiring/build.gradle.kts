plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-ksp")
}

dependencies {
    implementation(projects.feature.news.domain.impl)

    implementation(projects.core.logger.appLogger)
    implementation(projects.core.database)
    implementation(projects.core.commonJvm)
    implementation(projects.core.coroutines)
    implementation(projects.core.dagger)

    ksp(libs.daggerAnnotation)
}
