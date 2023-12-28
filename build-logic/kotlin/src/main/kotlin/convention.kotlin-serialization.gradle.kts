import gradle.kotlin.dsl.accessors._285dcef16d8875fee0ec91e18e07daf9.implementation
import gradle.kotlin.dsl.accessors._285dcef16d8875fee0ec91e18e07daf9.versionCatalogs

plugins {
    kotlin("plugin.serialization")
}

dependencies {
    implementation(versionCatalogs.named("libs").findLibrary("kotlinSerialization").get())
}
