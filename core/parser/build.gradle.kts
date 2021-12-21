plugins {
    javaLibrary()
    kotlinLibrary()
}

dependencies {
    implementation(Lib.jsonSimple)
    implementation(Lib.jsoup)
    implementation(Lib.coroutines)
}

