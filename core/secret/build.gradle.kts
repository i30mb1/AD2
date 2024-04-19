plugins {
    id("convention.android-library")
}

android {
    defaultConfig {
        ndk {
            abiFilters += setOf(
//                "armeabi-v7a",
//                "x86_64",
                "arm64-v8a",
                "x86",
            )
        }
    }
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }
}
