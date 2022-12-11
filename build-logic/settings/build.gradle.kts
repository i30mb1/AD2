plugins {
    `kotlin-dsl`
    `maven-publish`
}




publishing {
    publications {
        create<MavenPublication>("maven") {
            group = "n7"
            artifactId = "convention"
            version = "1.0"
            from(components["java"])
        }

    }
}