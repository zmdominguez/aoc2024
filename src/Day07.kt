import kotlin.math.pow

/**
 * Part 1
 *
 * What is their total calibration result?
 *
 * Part 2
 *
 */

private data class Calibration(
    val result: Long,
    val numbers: List<Long>,
)

private enum class Operation {
    SUM {
        override fun getResult(first: Long, second: Long) = first + second
    },
    MULTIPLY {
        override fun getResult(first: Long, second: Long) = first * second
    },
    CONCAT {
        override fun getResult(first: Long, second: Long) = (first.toString() + second.toString()).toLong()
    }
    ;

    abstract fun getResult(first: Long, second: Long): Long
}

fun main() {

    var equations = listOf<Calibration>()

    fun populateCalibrations(input: List<String>) {
        equations = input.map {
            val split = it.split(":")
            Calibration(
                result = split[0].toLong(),
                numbers = split[1].trim().split(" ").map { it.toLong() },
            )
        }
    }

    fun getValidEquationTotal(input: List<String>, operations: List<Operation>): Long {
        populateCalibrations(input)
        val dropCount = operations.size

        val validResults = mutableListOf<Long>()
        equations.forEach { equation ->
            val target = equation.result
            val numbers = equation.numbers

            var currentResult = mutableListOf(numbers[0])
            val nextResults = mutableListOf<Long>()

            numbers.drop(1).forEachIndexed { numberIndex, second ->
                nextResults.clear()
                currentResult.forEach { first ->
                    operations.forEach { operation ->
                        nextResults.add(operation.getResult(first, second))
                    }
                }

                currentResult = currentResult.drop(dropCount.toDouble().pow(numberIndex).toInt()).toMutableList()
                currentResult.addAll(nextResults)
            }

            if (currentResult.contains(target)) {
                validResults.add(target)
            }
        }

        return validResults.sum()
    }

    fun part1(input: List<String>): Long {
        val operations = listOf(Operation.SUM, Operation.MULTIPLY)
        return getValidEquationTotal(input, operations)
    }

    fun part2(input: List<String>): Long {
        val operations = Operation.entries
        return getValidEquationTotal(input, operations)
    }

    // Test if implementation meets criteria from the description:
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 3749L)
    check(part2(testInput) == 11387L)

    // Read the input from the `src/Day07.txt` file.
    val input = readInput("Day07")
    println("**** Part 1: Sum = ${part1(input)}") // 3598800864292
    println("**** Part 2: Sum = ${part2(input)}") // 340362529351427
}
