plugins {
    id("convention.kotlin-jvm")
    application
}

application {
    mainClass.set("n7.ad2.parser.heroes.HeroParserKt")
}

// ./gradlew :core:parser:runHeroes -PtestMode=false       → parse heroes (Valve datafeed + wiki trivia)
// ./gradlew :core:parser:runHeroes -Pheroes=Pudge,Lion    → parse only these heroes
// ./gradlew :core:parser:runHeroes -PparseResponses=true  → also re-download voice responses from the wiki
// ./gradlew :core:parser:runItems  -PcfClearance=<cookie>  → parse items
// Default test mode parses the first 3 heroes only

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
    val heroes = findProperty("heroes")?.toString() ?: System.getenv("HEROES") ?: ""
    val parseResponses = findProperty("parseResponses")?.toString() ?: System.getenv("PARSE_RESPONSES") ?: "false"
    if (cfClearance.isNotEmpty()) environment("CF_CLEARANCE", cfClearance)
    if (htmlDir.isNotEmpty()) environment("PARSER_HTML_DIR", htmlDir)
    if (heroes.isNotEmpty()) environment("HEROES", heroes)
    environment("TEST_MODE", testMode)
    environment("TEST_LIMIT", testLimit)
    environment("PARSE_RESPONSES", parseResponses)
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
