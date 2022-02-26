plugins {
    id("com.android.library")
    id("base-android-convention")
    id("kotlin-android")
    id("base-kotlin-convention")
}

android {
    androidComponents {
        beforeVariants { variantBuilder ->
            if (variantBuilder.buildType == "debug") variantBuilder.enabled = false
        }
    }
}