/**
 * Part 1
 *
 *
 * Part 2
 *
 */
fun main() {
    fun part1(input: List<String>): Int {
        return input.size
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // Test if implementation meets criteria from the description:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 1)
    check(part2(testInput) == 1)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day01")
    println("**** Part 1: Sum = ${part1(input)}")
    println("**** Part 2: Sum = ${part2(input)}")
}
