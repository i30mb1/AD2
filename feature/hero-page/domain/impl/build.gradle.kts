plugins {
    id("convention.android-library")
    id("convention.kotlin-serialization")
    id("n7.plugins.kotlin-ksp")
}

dependencies {
    api(projects.feature.heroPage.domain.api)

    implementation(projects.core.commonJvm)
    implementation(projects.core.coroutines)
    implementation(projects.core.logger.appLogger)
    implementation(projects.core.database)
    implementation(projects.core.appPreference)
    implementation(projects.core.repositories)
    implementation(projects.core.retrofit)
    implementation(projects.core.navigator)
    implementation(projects.core.dagger)
    implementation(projects.core.spanParser)

    implementation(projects.feature.heroes.domain.api)

    implementation(libs.kotlinSerialization)
    implementation(libs.room.ktx)

    ksp(libs.room.compiler)
    ksp(libs.daggerAnnotation)
}
