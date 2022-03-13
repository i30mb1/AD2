import com.android.build.gradle.BaseExtension

configure<BaseExtension> {
    compileSdkVersion(31)

    buildFeatures.viewBinding = true

    defaultConfig {
        minSdk = 23
        targetSdk = 31
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    lintOptions {
        isAbortOnError = false
        disable("UseCompoundDrawables")
    }
}

dependencies {
    lintChecks(project(Module.Core.rules))
}
