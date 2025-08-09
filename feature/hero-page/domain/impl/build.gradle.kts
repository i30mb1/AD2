plugins {
    id("convention.android-library")
    id("n7.plugins.kotlin-ksp")
}

dependencies {
    api(projects.feature.heroPage.domain.api)

    implementation(projects.core.database)
    implementation(projects.core.commonJvm)

    implementation(libs.coroutines)

}
