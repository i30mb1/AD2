plugins {
    javaLibrary()
    kotlinLibrary()
}

dependencies {
    compileOnly("com.android.tools.lint:lint-api:${Versions.lint}")
    compileOnly("com.android.tools.lint:lint-checks:${Versions.lint}")
}