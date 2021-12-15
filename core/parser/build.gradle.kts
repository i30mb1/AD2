plugins {
    javaLibrary()
    kotlin()
}

dependencies {
    implementation(Lib.jsonSimple)
    implementation(Lib.jsoup)
    implementation(Lib.coroutines)
}

