package n7.ad2.parser

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File

/**
 * Fetches a Dota 2 wiki page, bypassing Cloudflare.
 *
 * Strategy (tried in order):
 *  1. Local HTML cache (PARSER_HTML_DIR env or -Dparser.htmlDir)
 *  2. System curl — uses Windows Schannel TLS (browser-compatible fingerprint)
 *  3. Jsoup direct — fallback, likely blocked by Cloudflare
 *
 * To provide the Cloudflare cookie (required for curl):
 *   1. Open https://dota2.fandom.com in Chrome/Firefox
 *   2. DevTools → Application → Cookies → dota2.fandom.com
 *   3. Copy the value of 'cf_clearance'
 *   4. Pass as: -PcfClearance=<value>  or  env CF_CLEARANCE=<value>
 *
 * To use local HTML files (no network needed):
 *   - Save wiki pages from your browser to a directory
 *   - Pass: -PhtmlDir=<path>  or  env PARSER_HTML_DIR=<path>
 *   - File naming: Heroes page → heroes.html, Pudge page → pudge.html
 */
internal fun connectToWiki(rawUrl: String): Document {
    val url = rawUrl.replace(" ", "_")
    val cfClearance = System.getenv("CF_CLEARANCE") ?: System.getProperty("cf.clearance") ?: ""
    val cacheDir = System.getenv("PARSER_HTML_DIR") ?: System.getProperty("parser.htmlDir") ?: ""

    // 1. Local HTML cache
    if (cacheDir.isNotEmpty()) {
        val cached = readFromCache(url, cacheDir)
        if (cached != null) return cached
    }

    // 2. MediaWiki API — not behind the Cloudflare challenge, returns the rendered page body
    val apiDoc = fetchWithApi(url)
    if (apiDoc != null) return apiDoc

    // 3. curl (uses Windows Schannel TLS — browser-compatible)
    val curlHtml = fetchWithCurl(url, cfClearance)
    if (curlHtml != null) {
        val doc = Jsoup.parse(curlHtml, url)
        checkCloudflare(doc, url)
        return doc
    }

    // 4. Jsoup direct (likely blocked, but try anyway)
    println("  [WARN] curl not available, falling back to Jsoup")
    val doc = fetchWithJsoup(url, cfClearance)
    checkCloudflare(doc, url)
    return doc
}

// ─── MediaWiki API ──────────────────────────────────────────────────────────

private val wikiPageUrl = Regex("""^(https://[^/]+(?:/[a-z]{2})?)/wiki/(.+)$""")

/** https://dota2.fandom.com/ru/wiki/Pudge → https://dota2.fandom.com/ru/api.php?action=parse&page=Pudge */
private fun fetchWithApi(url: String): Document? {
    val match = wikiPageUrl.find(url) ?: return null
    val (base, page) = match.destructured
    val title = java.net.URLDecoder.decode(page, Charsets.UTF_8)
    val apiUrl = "$base/api.php?action=parse&prop=text&format=json&formatversion=2&redirects=1&page=" +
        java.net.URLEncoder.encode(title, Charsets.UTF_8)
    return try {
        val body = Jsoup.connect(apiUrl)
            .ignoreContentType(true)
            .ignoreHttpErrors(true)
            .timeout(30000)
            .maxBodySize(0)
            .execute()
            .body()
        val parse = (org.json.simple.parser.JSONParser().parse(body) as? org.json.simple.JSONObject)
            ?.get("parse") as? org.json.simple.JSONObject
        val html = parse?.get("text") as? String
        if (html == null) {
            println("  [API] no page for $title")
            null
        } else {
            println("  [API] OK — ${html.length} bytes ($title)")
            Jsoup.parse("<html><body><div id=\"mw-content-text\">$html</div></body></html>", url)
        }
    } catch (e: Exception) {
        println("  [API] failed for $title: ${e.message}")
        null
    }
}

// ─── curl ───────────────────────────────────────────────────────────────────

private fun fetchWithCurl(url: String, cfClearance: String): String? {
    val ua = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
        "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"

    val cmd = mutableListOf(
        "curl", "--silent", "--location",
        "--max-time", "30",
        "--user-agent", ua,
        "--header", "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8",
        "--header", "Accept-Language: en-US,en;q=0.9",
        "--header", "Sec-Fetch-Dest: document",
        "--header", "Sec-Fetch-Mode: navigate",
        "--header", "Sec-Fetch-Site: none",
        "--header", "Sec-Fetch-User: ?1",
        "--header", "Upgrade-Insecure-Requests: 1",
        "--compressed",
    )

    if (cfClearance.isNotEmpty()) {
        cmd += listOf("--cookie", "cf_clearance=$cfClearance")
        println("  [CF] Using cf_clearance via curl")
    }
    cmd += url

    return try {
        val process = ProcessBuilder(cmd)
            .redirectErrorStream(false)
            .start()
        val output = process.inputStream.bufferedReader().readText()
        val exitCode = process.waitFor()
        when {
            exitCode != 0 -> {
                println("  [curl] exit $exitCode for $url")
                null
            }
            output.length < 500 -> {
                println("  [curl] response too small (${output.length} bytes) for $url")
                null
            }
            else -> {
                println("  [curl] OK — ${output.length} bytes")
                output
            }
        }
    } catch (e: Exception) {
        println("  [curl] unavailable: ${e.message}")
        null
    }
}

// ─── Jsoup fallback ──────────────────────────────────────────────────────────

private fun fetchWithJsoup(url: String, cfClearance: String): Document {
    val conn = Jsoup.connect(url)
        .userAgent(
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36",
        )
        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
        .header("Accept-Language", "en-US,en;q=0.9")
        .header("Upgrade-Insecure-Requests", "1")
        .timeout(20000)
        .followRedirects(true)
        .ignoreHttpErrors(true)
        .ignoreContentType(true)
        .postDataCharset("UTF-8")

    if (cfClearance.isNotEmpty()) conn.cookie("cf_clearance", cfClearance)

    val response = conn.execute()
    println("  [HTTP] ${response.statusCode()} — $url")
    return response.parse()
}

// ─── Local HTML cache ────────────────────────────────────────────────────────

private fun readFromCache(url: String, cacheDir: String): Document? {
    val filename = urlToFilename(url)
    val file = File(cacheDir, filename)
    if (!file.exists() || file.length() < 500) return null
    println("  [CACHE] $filename (${file.length()} bytes)")
    return Jsoup.parse(file, "UTF-8", url)
}

/**
 * Converts a wiki URL to a short filename.
 *   https://dota2.fandom.com/wiki/Heroes         → heroes.html
 *   https://dota2.fandom.com/wiki/Pudge          → pudge.html
 *   https://dota2.fandom.com/wiki/Pudge/Responses → pudge_responses.html
 */
internal fun urlToFilename(url: String): String {
    val path = url
        .removePrefix("https://dota2.fandom.com/wiki/")
        .removePrefix("https://dota2.fandom.com/ru/wiki/")
        .replace("/", "_")
        .replace(Regex("[^a-zA-Z0-9_\\-.]"), "_")
        .lowercase()
        .trimEnd('_')
    return "$path.html"
}

// ─── Cloudflare detection ────────────────────────────────────────────────────

private fun checkCloudflare(doc: Document, url: String) {
    val isChallenge = doc.title() == "Just a moment..." ||
        doc.select("script[src*='challenge-platform']").isNotEmpty()
    if (!isChallenge) return

    val filename = urlToFilename(url)
    val cacheDir = System.getenv("PARSER_HTML_DIR") ?: System.getProperty("parser.htmlDir") ?: "parser_html"

    error(
        "\n\nCloudflare is blocking access to: $url\n\n" +
            "Option A — Provide the cf_clearance cookie:\n" +
            "  1. Open the URL above in Chrome/Firefox (the browser solves the challenge)\n" +
            "  2. DevTools (F12) → Application → Cookies → dota2.fandom.com\n" +
            "  3. Copy the value of 'cf_clearance'\n" +
            "  4. Re-run:  .\\gradlew :core:parser:runHeroes -PcfClearance=<value>\n\n" +
            "Option B — Save the page manually:\n" +
            "  1. Open the URL above in your browser\n" +
            "  2. Ctrl+S → 'Webpage, HTML Only' → save as $cacheDir\\$filename\n" +
            "  3. Re-run:  .\\gradlew :core:parser:runHeroes -PhtmlDir=$cacheDir\n",
    )
}
