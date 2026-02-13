package n7.ad2.parser.algorithm

import java.io.File

fun main() {
    val _text = File("input.txt").readText()
    var result = true

    File("output.txt").writeText(result.toString())
}
