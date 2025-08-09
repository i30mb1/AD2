plugins {
    id("convention.kotlin-jvm")
    id("n7.plugins.kotlin-ksp")
}

dependencies {
    api(libs.coroutines)

    implementation(projects.core.dagger)
    implementation(libs.test.junit)
    api(libs.test.coroutines)

    ksp(libs.daggerAnnotation)

    testImplementation(libs.test.junit)
    testImplementation(libs.test.coroutines.debug)
    testImplementation(libs.test.truth.jvm)
}
