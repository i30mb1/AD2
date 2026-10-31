plugins {
    id("convention.android-library")
    id("convention.kotlin-serialization")
    id("n7.plugins.kotlin-ksp")
}

dependencies {
    api(projects.feature.games.leaderboard.api)

    implementation(libs.room.ktx)
    implementation(libs.coroutines)

    ksp(libs.room.compiler)

    testImplementation(libs.test.junit)
    testImplementation(libs.test.truth)
    testImplementation(libs.test.core)
    testImplementation(libs.test.robolectric)
    testImplementation(libs.test.coroutines)
}
