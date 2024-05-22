plugins {
    kotlin("plugin.serialization")
}

dependencies {
    add("implementation", versionCatalogs.named("libs").findLibrary("kotlinSerialization").get())
}
