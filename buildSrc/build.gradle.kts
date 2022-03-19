plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        register("file-line-prefix-plugin-registration") {
            implementationClass = "FileLinePrefixPlugin"
            id = "file-line-prefix-plugin"
        }
    }
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation("com.android.tools.build:gradle:7.0.4")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.30")
}