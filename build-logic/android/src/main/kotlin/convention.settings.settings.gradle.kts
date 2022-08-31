dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()  // тут лежит kotlin-stdlib-jdk8
    }
    versionCatalogs {
        create("libs") {
            from(files("build-logic/gradle/libs.versions.toml"))
        }
    }
}

pluginManagement {
    repositories {
        gradlePluginPortal() // kotlin-dsl, kotlin, jvm, kapt, org.jetbrains.kotlin.android
        google()  // com.android.library
    }

}