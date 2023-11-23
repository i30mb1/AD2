plugins {
    id("convention.kotlin-jvm")
    id("n7.plugins.kotlin-kapt")
}

dependencies {
    api(libs.coroutines)

    implementation(projects.core.dagger)
    implementation(libs.testJunit)
    api(libs.coroutinesTest)

    kapt(libs.daggerAnnotation)
}
