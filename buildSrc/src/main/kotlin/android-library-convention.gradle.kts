plugins {
    id("com.android.library")
    id("base-android-convention")
    id("base-kotlin-convention")
}

android {
    androidComponents {
        beforeVariants { variantBuilder ->
            if (variantBuilder.buildType == "debug") variantBuilder.enable = false
        }
    }
}