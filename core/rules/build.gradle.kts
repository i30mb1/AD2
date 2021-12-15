plugins {
    javaLibrary()
    kotlin()
}

dependencies {
    compileOnly("com.android.tools.lint:lint-api:${Versions.lint}")
    compileOnly("com.android.tools.lint:lint-checks:${Versions.lint}")
}