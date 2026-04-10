plugins {
    id("convention.kotlin-jvm")
    application
}

application {
    mainClass.set("n7.ad2.parser.heroes.HeroParserKt")
}

// ./gradlew :core:parser:runHeroes -PcfClearance=<cookie>  → parse heroes
// ./gradlew :core:parser:runItems  -PcfClearance=<cookie>  → parse items
// Set TEST_MODE=false to parse all heroes (default: true = first 3 only)

tasks.register<JavaExec>("runHeroes") {
    group = "parser"
    description = "Parse Dota 2 heroes from the wiki and generate JSON data files"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("n7.ad2.parser.heroes.HeroParserKt")
    workingDir = rootProject.projectDir  // assetsDatabase uses user.dir — must point to repo root
    val cfClearance = findProperty("cfClearance")?.toString() ?: System.getenv("CF_CLEARANCE") ?: ""
    val htmlDir = findProperty("htmlDir")?.toString() ?: System.getenv("PARSER_HTML_DIR") ?: ""
    val testMode = findProperty("testMode")?.toString() ?: System.getenv("TEST_MODE") ?: "true"
    val testLimit = findProperty("testLimit")?.toString() ?: System.getenv("TEST_LIMIT") ?: "3"
    if (cfClearance.isNotEmpty()) environment("CF_CLEARANCE", cfClearance)
    if (htmlDir.isNotEmpty()) environment("PARSER_HTML_DIR", htmlDir)
    environment("TEST_MODE", testMode)
    environment("TEST_LIMIT", testLimit)
}

tasks.register<JavaExec>("runItems") {
    group = "parser"
    description = "Parse Dota 2 items from the wiki and generate JSON data files"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("n7.ad2.parser.items.ParseItemsKt")
    workingDir = rootProject.projectDir  // assetsDatabase uses user.dir — must point to repo root
    val cfClearance = findProperty("cfClearance")?.toString() ?: System.getenv("CF_CLEARANCE") ?: ""
    val htmlDir = findProperty("htmlDir")?.toString() ?: System.getenv("PARSER_HTML_DIR") ?: ""
    if (cfClearance.isNotEmpty()) environment("CF_CLEARANCE", cfClearance)
    if (htmlDir.isNotEmpty()) environment("PARSER_HTML_DIR", htmlDir)
}

dependencies {
    implementation(libs.jsonSimple)
    implementation(libs.jsoup)
    implementation(libs.coroutines)
    testImplementation(libs.test.junit)
    testImplementation(libs.jsoup)
}
