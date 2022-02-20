plugins {
    id("file-line-prefix-plugin")
    id("com.osacky.doctor") version "0.7.3"
    id("com.autonomousapps.dependency-analysis") version "1.0.0-rc02"
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