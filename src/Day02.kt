import kotlin.math.absoluteValue

/**
 * Part 1
 *
 * A report only counts as safe if both of the following are true:
 *
 * The levels are either all increasing or all decreasing.
 * Any two adjacent levels differ by at least one and at most three.
 *
 * Part 2
 *
 * Update your analysis by handling situations where the Problem Dampener can remove a single level from unsafe reports.
 * How many reports are now safe?
 *
 */

private enum class Direction { INCREASE, DECREASE }

fun main() {
    val reports = mutableListOf<List<Int>>()

    fun getReports(input: List<String>) {
        reports.clear()

        input.forEach { line ->
            val report = line.split(" ")
                .filter { it.isNotBlank() || it.isNotEmpty() }
                .map { it.toInt() }
            reports.add(report)
        }
    }

    fun isSequenceValid(report: List<Int>): Boolean {
        var currentDirection: Direction? = null
        report.forEachIndexed { index, _ ->
            val current = report[index]

            if (index + 1 == report.size) {
                return true
            }

            val next = report[index + 1]

            val diff = current - next
            val direction = if (diff > 0) Direction.DECREASE else Direction.INCREASE
            if (index == 0) {
                currentDirection = direction
            } else {
                if (currentDirection != direction) {
                    return false
                }
            }

            val isValid = diff.absoluteValue in 1..3
            if (!isValid) return false
        }

        return true
    }

    fun part1(input: List<String>): Int {
        getReports(input)

        var safeCount = 0
        reports.forEach { report ->

            if (isSequenceValid(report)) {
                safeCount++
            }
        }

        return safeCount
    }

    fun part2(input: List<String>): Int {
        getReports(input)

        var safeCount = 0
        reports.forEach { report ->


            val isSequenceValid = isSequenceValid(report)
            if (isSequenceValid) {
                safeCount++
                return@forEach
            } else {
                // Drop each element starting from the first one
                // And check if there's a way to make the report valid
                report.forEachIndexed { index, _ ->
                    val workingReport = report.toMutableList()
                    workingReport.removeAt(index)

                    val isWorkingReportValid = isSequenceValid(workingReport)
                    if (isWorkingReportValid) {
                        safeCount++
                        return@forEach
                    }
                }
            }
        }

        return safeCount
    }

    // Test if implementation meets criteria from the description:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

    // Read the input from the `src/Day02.txt` file.
    val input = readInput("Day02")
    println("**** Part 1: Safe levels = ${part1(input)}") // 334
    println("**** Part 2: Safe levels = ${part2(input)}") // 400
}
