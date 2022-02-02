plugins {
    id("file-line-prefix-plugin")
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