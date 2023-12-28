import com.android.build.gradle.LibraryExtension

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
                if (project.name == "ui" && project.path.contains(":core:").not()) return@beforeVariants
                variantBuilder.enable = false
            }
        }
    }
    packaging {
        resources.excludes.add("META-INF/*")
    }
}

configure<LibraryExtension> {
    buildTypes {
        getByName("debug") {
            matchingFallbacks.add("release")
        }
    }
}
