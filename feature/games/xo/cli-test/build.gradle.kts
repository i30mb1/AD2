plugins {
    id("convention.kotlin-jvm")
}

dependencies {
    implementation(projects.feature.games.xo.domain.api)
    implementation(projects.core.coroutines)
    implementation(projects.core.commonJvm)

    implementation(libs.coroutines)
}
