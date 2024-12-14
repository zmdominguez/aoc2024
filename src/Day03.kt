/**
 * Part 1
 *
 * Scan the corrupted memory for uncorrupted mul instructions. What do you get if you add up
 * all of the results of the multiplications?
 *
 * Part 2
 *
 * What do you get if you add up all of the results of just the enabled multiplications?
 */
fun main() {
    val MUL_PATTERN = """mul\(\d{1,3},\d{1,3}\)""".toRegex()

    fun findMatchesInLine(line: String): Int {
        var sum = 0
        val matchResults = MUL_PATTERN.findAll(line)
        matchResults.forEach { result ->
            val matchValue = result.value
            val operands = matchValue.removePrefix("mul(")
                .removeSuffix(")").split(",")
                .map { it.toInt() }
            sum += operands[0] * operands[1]
        }
        return sum
    }

    fun part1(input: List<String>): Int {
        var sum = 0

        input.forEach { line ->
            sum += findMatchesInLine(line)
        }
        return sum
    }

    fun part2(input: List<String>): Int {
        var sum = 0
        val enclosedPattern = """(?<=do\(\)).*?(?=don't\(\))""".toRegex()
        val inputLine = input.joinToString("")

        val matchResults = enclosedPattern.findAll("do()" + inputLine + "don't()")
        matchResults.forEach { match ->
            sum += findMatchesInLine(match.value)
        }

        return sum
    }

    // Test if implementation meets criteria from the description:
    check(part1(readInput("Day03_test")) == 161)
    check(part2(readInput("Day03b_test")) == 48)

    // Read the input from the `src/Day03.txt` file.
    val input = readInput("Day03")
    println("**** Part 1: Sum = ${part1(input)}") // 164730528
    println("**** Part 2: Sum = ${part2(input)}") // 70478672
}
