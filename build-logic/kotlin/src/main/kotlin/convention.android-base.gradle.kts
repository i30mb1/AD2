import com.android.build.gradle.BaseExtension
import gradle.kotlin.dsl.accessors._44fb0a05fcd9e15986a76c748cb18b72.lintChecks

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