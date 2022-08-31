plugins {
    id("bump-version-plugin")
    id("measure-build-plugin")
    id("convention.detekt")
    id("com.android.test") version "7.1.2" apply false
    id("org.jetbrains.kotlin.android") version "1.6.21" apply false
//    id("com.osacky.doctor") version "0.7.3"
//    id("com.autonomousapps.dependency-analysis") version "1.0.0-rc02"
    // https://arrow-kt.io/docs/meta/analysis/
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

val printFromGradleProperties by tasks.registering {
    group = "n7"
    doLast {
        println("Hello ${project.property("key")}")
    }
}