plugins {
    id("convention.kotlin-jvm")
}

dependencies {
    compileOnly(libs.lintApi)
    compileOnly(libs.lintChecks)
}