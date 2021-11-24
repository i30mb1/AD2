plugins {
    id("java-library")
    id("kotlin")
}

dependencies {
    compileOnly("com.android.tools.lint:lint-api:${Versions.lint}")
    compileOnly("com.android.tools.lint:lint-checks:${Versions.lint}")
}