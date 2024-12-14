import kotlin.math.absoluteValue

/**
 * Part 1
 *
 * Maybe the lists are only off by a small amount! To find out, pair up the numbers and measure how far apart they are.
 *
 * What is the total distance between your lists?
 *
 * Part 2
 *
 * Calculate a total similarity score by adding up each number in the left list after multiplying it by the number of times that number appears in the right list.
 *
 */

fun main() {

    val columnA = mutableListOf<Int>()
    val columnB = mutableListOf<Int>()

    fun splitIntoColumns(input: List<String>) {
        columnA.clear()
        columnB.clear()

        input.forEach { line ->
            val pair = line.split(" ")
                .filter { it.isNotBlank() || it.isNotEmpty() }
                .map { it.toInt() }
            columnA.add(pair.first())
            columnB.add(pair.last())
        }
    }

    fun part1(input: List<String>): Int {

        splitIntoColumns(input)

        columnA.sortDescending()
        columnB.sortDescending()

        var sumOfDistances = 0
        columnA.forEachIndexed { index, value ->
            sumOfDistances += (value - columnB[index]).absoluteValue
        }
        return sumOfDistances
    }

    fun part2(input: List<String>): Int {

        splitIntoColumns(input)

        var similarityScore = 0
        columnA.forEach { value ->
            val countInB = columnB.count { it == value }
            similarityScore += value * countInB
        }

        return similarityScore
    }

    // Test if implementation meets criteria from the description:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 11)
    check(part2(testInput) == 31)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day01")
    println("**** Part 1: Sum = ${part1(input)}") // 3508942
    println("**** Part 2: Sum = ${part2(input)}") // 26593248
}
