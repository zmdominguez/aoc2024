/**
 * Part 1
 *
 * What do you get if you add up the middle page number from those correctly-ordered updates?
 *
 * Part 2
 *
 * What do you get if you add up the middle page numbers after correctly ordering just those updates?
 */

fun main() {

    val rules = mutableListOf<List<Int>>()
    val updates = mutableListOf<List<Int>>()

    fun populateRulesAndUpdates(input: List<String>) {
        rules.clear()
        updates.clear()

        input.forEach { line ->
            when {
                line.contains("|") -> {
                    rules.add(line.split("|").map { it.toInt() })
                }

                line.contains(",") -> {
                    updates.add(line.split(",").map { it.toInt() })
                }
            }
        }
    }

    /** All the rules that have this [Int] in either position */
    fun Int.findAllApplicableRules() = rules.filter { this in it }

    fun part1(input: List<String>): Int {
        populateRulesAndUpdates(input)

        var middleNumberSum = 0

        updates.forEach { update ->
            update.forEachIndexed { entryIndex, entry ->
                val applicableRules = entry.findAllApplicableRules()

                if (entryIndex == 0) {
                    // This entry has rules that says some other page in the same update should come before it
                    val entryHasBefore = applicableRules.filter { rule ->
                        rule[1] == entry
                                && rule[0] in update.drop(1)
                    }
                    if (entryHasBefore.isNotEmpty()) return@forEach
                }

                // These are the rules that say this entry should come before the second number in the rule
                val rulesBefore = applicableRules.filter { rule -> rule[0] == entry }
                val pagesBefore = update.subList(0, entryIndex)

                // If there are any entries in the pagesBefore that is the second number in rulesBefore, NOT ALLOWED
                if ((rulesBefore.map<List<Int>, Int> { rule -> rule[1] } intersect pagesBefore).isNotEmpty()) return@forEach

                val rulesAfter = applicableRules.filter { rule -> rule[1] == entry }
                val pagesAfter = update.subList(entryIndex + 1, update.size)

                // If there are any entries in the pagesAfter that is the first number in rulesAfter, NOT ALLOWED
                if ((rulesAfter.map<List<Int>, Int> { rule -> rule[0] } intersect pagesAfter).isNotEmpty()) return@forEach

                if (entryIndex == update.lastIndex) {
                    middleNumberSum += update[update.size / 2]
                }
            }
        }

        return middleNumberSum
    }

    fun getAllInvalidRowIndices(updates: List<List<Int>>): List<Int> {
        val invalidUpdateIndices = mutableListOf<Int>()

        updates.forEachIndexed { index, update ->
            for (entryIndex in update.indices) {
                val entry = update[entryIndex]

                val applicableRules = entry.findAllApplicableRules()

                if (entryIndex == 0) {
                    // This entry has rules that says some other page in the same update should come before it
                    val entryHasBefore = applicableRules.filter { rule ->
                        rule[1] == entry
                                && rule[0] in update.drop(1)
                    }
                    if (entryHasBefore.isNotEmpty()) {
                        invalidUpdateIndices.add(index)
                        return@forEachIndexed
                    }
                }

                // These are the rules that say this entry should come before the second number in the rule
                val rulesBefore = applicableRules.filter { rule -> rule[0] == entry }
                val pagesBefore = update.subList(0, entryIndex)

                // If there are any entries in the pagesBefore that is the second number in rulesBefore, NOT ALLOWED
                if ((rulesBefore.map<List<Int>, Int> { rule -> rule[1] } intersect pagesBefore).isNotEmpty()) {
                    invalidUpdateIndices.add(index)
                    return@forEachIndexed
                }

                val rulesAfter = applicableRules.filter { rule -> rule[1] == entry }
                val pagesAfter = update.subList(entryIndex + 1, update.size)

                // If there are any entries in the pagesAfter that is the first number in rulesAfter, NOT ALLOWED
                if ((rulesAfter.map<List<Int>, Int> { rule -> rule[0] } intersect pagesAfter).isNotEmpty()) {
                    invalidUpdateIndices.add(index)
                    return@forEachIndexed
                }
            }
        }
        return invalidUpdateIndices
    }

    fun part2(input: List<String>): Int {
        populateRulesAndUpdates(input)

        val invalidUpdateIndices = getAllInvalidRowIndices(updates.toList())
        val invalidRows = mutableListOf<List<Int>>()
        invalidUpdateIndices.forEach { index ->
            invalidRows.add(updates[index])
        }

        val middleNumbers = mutableListOf<Int>()

        invalidUpdateIndices.forEach { updateIndex ->
            val updateToFix = updates[updateIndex].toMutableList()
            val fixedUpdates = mutableListOf<Int>()

            // Seed with the first number
            fixedUpdates.add(updateToFix[0])

            for (entryIndex in 1..updateToFix.lastIndex) {
                val entry = updateToFix[entryIndex]

                val applicableRules = entry.findAllApplicableRules()

                // Get all the rules that contain the fixed numbers so far
                // Any number in rule[1] comes AFTER the current number
                val rulesAfter = applicableRules.filter { rule -> rule[0] == entry }
                    .filter { rule -> rule[1] in fixedUpdates }

                // Get all the rules that contain the fixed numbers so far
                // Any number in rule[0] comes BEFORE the current number
                val rulesBefore = applicableRules.filter { rule -> rule[1] == entry }
                    .filter { rule -> rule[0] in fixedUpdates }

                val tentativeInsertIndex = emptyList<Int>().toMutableList()
                val allBefores = emptyList<Int>().toMutableList()
                if (rulesAfter.isNotEmpty()) {
                    // [entry] is to be inserted before the existing values
                    rulesAfter.forEach { rule ->
                        val shouldComeBefore = rule[1]
                        val nextEntryIndex = fixedUpdates.indexOf(shouldComeBefore)
                        val thisEntryIndex = if (nextEntryIndex == 0) 0 else nextEntryIndex - 1
                        allBefores.add(thisEntryIndex)
                    }
                }

                if (allBefores.isNotEmpty()) {
                    tentativeInsertIndex.add(allBefores.min())
                }

                // Check if there are any numbers in the fixed index that should go first
                // rule[1] is the current number we are tying to insert
                val allAfters = emptyList<Int>().toMutableList()
                if (rulesBefore.isNotEmpty()) {
                    // [entry] is to be inserted after the existing fixed values
                    rulesBefore.forEach { rule ->
                        val shouldComeAfter = rule[0]
                        val nextEntryIndex = fixedUpdates.indexOf(shouldComeAfter)
                        val thisEntryIndex =
                            if (nextEntryIndex + 1 == fixedUpdates.size) fixedUpdates.size else nextEntryIndex + 1
                        allAfters.add(thisEntryIndex)
                    }
                }

                if (allAfters.isNotEmpty()) {
                    tentativeInsertIndex.add(allAfters.max())
                }

                fixedUpdates.add(tentativeInsertIndex.max(), entry)
            }

            middleNumbers.add(fixedUpdates[fixedUpdates.size / 2])
        }

        return middleNumbers.sum()
    }

    // Test if implementation meets criteria from the description:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == 143)
    check(part2(testInput) == 123)

    // Read the input from the `src/Day05.txt` file.
    val input = readInput("Day05")
    println("**** Part 1: Sum = ${part1(input)}") // 4689
    println("**** Part 2: Sum = ${part2(input)}") // 6336
}
