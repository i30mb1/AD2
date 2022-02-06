import com.android.build.gradle.BaseExtension

configure<BaseExtension> {
    compileSdkVersion(31)

    buildFeatures.viewBinding = true

    defaultConfig {
        minSdk = 23
        targetSdk = 31
    }

    lintOptions {
        isAbortOnError = false
    }
}
