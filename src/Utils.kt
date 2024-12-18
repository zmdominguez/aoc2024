import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readText

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/inputs/$name.txt").readText().trim().lines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)


fun String?.indicesOf(substr: String, ignoreCase: Boolean = true): List<Int> {
    return this?.let {
        val regex = if (ignoreCase) Regex(substr, RegexOption.IGNORE_CASE) else Regex(substr)
        regex.findAll(this).map { it.range.first }.toList()
    } ?: emptyList()
}

fun String.replaceCharAt(index: Int, char: String) = this.replaceRange(index..index, char)

data class Location(val row: Int, val column: Int) {
    override fun toString(): String {
        return "[row = $row, column = $column]"
    }
}

fun List<String>.getStringToLeftOf(lastLocation: Location): String {
    return this[lastLocation.row].substring(0..<lastLocation.column)
}

fun List<String>.getStringToRightOf(lastLocation: Location): String {
    return this[lastLocation.row].substring(lastLocation.column + 1)
}

fun List<String>.getStringAbove(lastLocation: Location): String {
    var tempLine = ""
    for (index in 0..<lastLocation.row) {
        tempLine += this[index][lastLocation.column]
    }

    // first char is from y=0
    return tempLine
}

fun List<String>.getStringBelow(lastLocation: Location, startInclusive: Boolean = false): String {
    var tempLine = ""
    val firstIndexY = if (startInclusive) lastLocation.row else lastLocation.row + 1
    for (index in firstIndexY..<this.size) {
        tempLine += this[index][lastLocation.column]
    }
    // first char is from y+1
    return tempLine
}