plugins {
    javaLibrary()
    kotlin()
}

dependencies {
    implementation(kotlin("stdlib-jdk7"))
    implementation(Lib.jsonSimple)
    implementation(Lib.jsoup)
    implementation(Lib.coroutines)
}

