plugins {
    id("convention.android-library")
}

dependencies {
    api(projects.feature.games.room.api)

    implementation(projects.feature.games.xo.domain.impl)
    implementation(libs.coroutines)

    testImplementation(libs.test.junit)
    testImplementation(libs.test.truth.jvm)
    testImplementation(libs.test.coroutines)
    testImplementation(libs.test.coroutines.debug)
}
