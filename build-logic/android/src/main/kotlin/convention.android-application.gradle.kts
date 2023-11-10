import com.android.build.api.dsl.ApplicationExtension

plugins {
    id("com.android.application")
    id("convention.android-base")
    id("convention.kotlin-base")
}

configure<ApplicationExtension> {
    buildTypes {
        getByName("debug") {
            matchingFallbacks += "release"
        }
    }
    lint {
        abortOnError = false
        disable += "UseCompoundDrawables"
    }
}
