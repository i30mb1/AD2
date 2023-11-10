plugins {
    id("convention.kotlin-jvm")
    id("n7.plugins.kotlin-kapt")
}

dependencies {
    api(libs.coroutines)

    implementation(project(Module.Core.dagger))
    implementation(libs.testJunit)
    implementation(libs.coroutinesTest)

    kapt(libs.daggerAnnotation)
}
