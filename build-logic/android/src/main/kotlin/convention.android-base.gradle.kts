import com.android.build.gradle.BaseExtension

plugins {
    kotlin("android")
}

configure<BaseExtension> {
    compileSdkVersion(35)

    buildFeatures.viewBinding = true

    defaultConfig {
        minSdk = 31
        targetSdk = 35
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    lintOptions {
        disable(
            "CoroutineCreationDuringComposition", // Disable problematic Compose lint detector
            "RestrictedApi",
            "UnknownNullness"
        )
        isAbortOnError = false
        isWarningsAsErrors = false
    }
}

dependencies {
    add("lintChecks", project(":core:rules"))
}

tasks.withType<Test> {
    testLogging.events("passed", "skipped", "failed", "standardOut", "standardError")
    systemProperty("junit.platform.output.capture.stdout", "true")
    systemProperty("junit.platform.output.capture.stderr", "true")
}
