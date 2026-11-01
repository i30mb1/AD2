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
    implementation(projects.feature.games.xo.domain.api)
    implementation(projects.feature.games.xo.domain.wiring)
    implementation(projects.feature.games.mix.domain.wiring)
    implementation(projects.feature.games.room.api)
    implementation(projects.feature.games.leaderboard.api)
    implementation(projects.feature.heroes.domain.api)

    ksp(libs.daggerAnnotation)

    testImplementation(libs.test.junit)
    testImplementation(libs.test.truth.jvm)
    // the rooms over real sockets on the loopback for the party tests
    testImplementation(projects.feature.games.room.impl)
}
