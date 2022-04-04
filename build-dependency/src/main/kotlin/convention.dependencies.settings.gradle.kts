dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()  // тут лежит kotlin-stdlib-jdk8
//        exclusiveContent {
//            forRepository { google() }
//            filter {
//                includeGroupByRegex("com\\.android.*")
//                includeGroupByRegex("com\\.google\\.android\\..*")
//            }
//        }
    }
}