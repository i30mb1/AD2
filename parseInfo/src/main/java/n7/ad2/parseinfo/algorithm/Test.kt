package n7.ad2.parseinfo.algorithm

import java.io.File

fun main() {
    val text = File("input.txt").readText()
    var result = true

    File("output.txt").writeText(result.toString())
}