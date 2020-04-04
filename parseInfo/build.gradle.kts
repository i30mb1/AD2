plugins {
    javaLibrary()
    kotlin()
}

dependencies {
    implementation(Lib.kotlinStdlib)
    implementation(Lib.jsonSimple)
    implementation(Lib.jsoup)
    implementation(Lib.coroutines)
}

