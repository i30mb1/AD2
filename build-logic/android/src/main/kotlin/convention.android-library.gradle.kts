plugins {
    id("com.android.library")
    id("convention.android-base")
    id("convention.kotlin-base")
}

android {
    val projectNameFormatted = project.path.drop(1).replace(Regex("[-:]"), ".")
    namespace = "$applicationID.$projectNameFormatted"
    androidComponents {
        beforeVariants { variantBuilder ->
            if (variantBuilder.buildType == "debug") {
                if (project.name == "demo" || project.name == "ui" || project.name == "impl") return@beforeVariants
                variantBuilder.enable = false
            }
        }
    }
    packaging {
        resources.excludes.add("META-INF/*")
    }
    buildTypes {
        getByName("debug") {
            matchingFallbacks.add("release")
        }
    }
}
