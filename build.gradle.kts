allprojects {
    repositories {
        jcenter()
        google()
        maven("https://maven.google.com")
    }
}

buildscript {
    repositories {
        jcenter()
        google()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:3.6.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.71")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

