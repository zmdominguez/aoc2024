/**
 * Part 1
 *
 * How many times does XMAS appear?
 *
 * Part 2
 *
 * How many times does an X-MAS appear?
 *
 */

private data class Location(val row: Int, val column: Int) {
    override fun toString(): String {
        return "[row = $row, column = $column]"
    }
}

private sealed class WordDirection {
    data object North : WordDirection() {
        override fun getNextLocation(startLocation: Location) = Location(startLocation.row-1, startLocation.column)
        override fun getOppositeDirection() = South
    }
    data object NorthEast : WordDirection() {
        override fun getNextLocation(startLocation: Location) = Location(startLocation.row-1, startLocation.column+1)
        override fun getOppositeDirection() = SouthWest
    }
    data object East : WordDirection() {
        override fun getNextLocation(startLocation: Location) = Location(startLocation.row, startLocation.column+1)
        override fun getOppositeDirection() = West
    }
    data object SouthEast : WordDirection() {
        override fun getNextLocation(startLocation: Location) = Location(startLocation.row+1, startLocation.column+1)
        override fun getOppositeDirection() = NorthWest
    }
    data object South : WordDirection() {
        override fun getNextLocation(startLocation: Location) = Location(startLocation.row+1, startLocation.column)
        override fun getOppositeDirection() = North
    }
    data object SouthWest : WordDirection() {
        override fun getNextLocation(startLocation: Location) = Location(startLocation.row+1, startLocation.column-1)
        override fun getOppositeDirection() = NorthEast
    }
    data object West : WordDirection() {
        override fun getNextLocation(startLocation: Location) = Location(startLocation.row, startLocation.column-1)
        override fun getOppositeDirection() = East
    }
    data object NorthWest : WordDirection() {
        override fun getNextLocation(startLocation: Location) = Location(startLocation.row-1, startLocation.column-1)
        override fun getOppositeDirection() = SouthEast
    }

    abstract fun getNextLocation(startLocation: Location): Location
    abstract fun getOppositeDirection(): WordDirection
}

private data class PossibleLocation(
    val direction: WordDirection,
    val lastLocation: Location,
)

fun main() {
    val tableInput = mutableListOf<String>()

    fun findNextLetterLocation(possibleLocation: PossibleLocation, letterToFind: Char): Location? {

        // Find the next letter from the provided location, in the direction given
        val lastLetterLocation = possibleLocation.lastLocation
        val direction = possibleLocation.direction

        // If direction is northwards and we are already at the top, no more space
        if (lastLetterLocation.row == 0 &&
            direction in listOf(WordDirection.North, WordDirection.NorthWest, WordDirection.NorthEast)) {
            return null
        }

        // If direction is southwards and we are already at the very bottom, no more space
        if (lastLetterLocation.row == tableInput.size - 1 &&
            direction in listOf(WordDirection.South, WordDirection.SouthWest, WordDirection.SouthEast)) {
            return null
        }

        // If going to the right, and already at the end, no more space
        if (lastLetterLocation.column == tableInput[lastLetterLocation.row].length - 1 &&
            direction in listOf(WordDirection.East, WordDirection.NorthEast, WordDirection.SouthEast)) {
            return null
        }

        // If going to the left, and already at the end, no more space
        if (lastLetterLocation.column == 0 &&
            direction in listOf(WordDirection.West, WordDirection.NorthWest, WordDirection.SouthWest)) {
            return null
        }

        // Otherwise, there is space for the next letter
        // Check what it is
        val nextLocation = direction.getNextLocation(lastLetterLocation)
        return if (tableInput[nextLocation.row][nextLocation.column] == letterToFind) {
            nextLocation
        } else {
            null
        }
    }

    /**
     * Finds the character M from the given location
     */
    fun findInitialDirection(location: Location): List<PossibleLocation> {
        val possibleInitialLocations = mutableListOf<PossibleLocation>()
        val secondLetter = 'M'
        var direction: WordDirection? = null

        // Find the "M" from location of X
        if (location.row != 0) {
            val above = tableInput[location.row - 1]
            var charAbove = above[location.column]
            if (charAbove == secondLetter) {
                direction = WordDirection.North
                possibleInitialLocations.add(PossibleLocation(direction, direction.getNextLocation(location)))
            }

            if (location.column != above.length - 1) {
                charAbove = above[location.column + 1]
                if (charAbove == secondLetter) {
                    direction = WordDirection.NorthEast
                    possibleInitialLocations.add(PossibleLocation(direction, direction.getNextLocation(location)))
                }
            }

            if (location.column > 0) {
                charAbove = above[location.column - 1]
                if (charAbove == secondLetter) {
                    direction = WordDirection.NorthWest
                    possibleInitialLocations.add(PossibleLocation(direction, direction.getNextLocation(location)))
                }
            }
        }

        // If there are more rows below
        if (location.row != tableInput.size - 1) {
            val below = tableInput[location.row + 1]
            var charBelow = below[location.column]
            if (charBelow == secondLetter) {
                direction = WordDirection.South
                possibleInitialLocations.add(PossibleLocation(direction, direction.getNextLocation(location)))
            }

            if (location.column != below.length - 1) {
                charBelow = below[location.column + 1]
                if (charBelow == secondLetter) {
                    direction = WordDirection.SouthEast
                    possibleInitialLocations.add(PossibleLocation(direction, direction.getNextLocation(location)))
                }
            }

            if (location.column > 0) {
                charBelow = below[location.column - 1]
                if (charBelow == secondLetter) {
                    direction = WordDirection.SouthWest
                    possibleInitialLocations.add(PossibleLocation(direction, direction.getNextLocation(location)))
                }
            }
        }

        if (location.column != tableInput[location.row].length - 1) {
            val charInRow = tableInput[location.row][location.column + 1]
            if (charInRow == secondLetter) {
                direction = WordDirection.East
                possibleInitialLocations.add(PossibleLocation(direction, direction.getNextLocation(location)))
            }
        }

        if (location.column > 0) {
            val charInRow = tableInput[location.row][location.column - 1]
            if (charInRow == secondLetter) {
                direction = WordDirection.West
                possibleInitialLocations.add(PossibleLocation(direction, direction.getNextLocation(location)))
            }
        }

        return possibleInitialLocations
    }

    fun part1(input: List<String>): Int {

        tableInput.clear()

        // Convert the input to a list of strings
        input.forEach { line ->
            tableInput.add(line)
        }

        var validCount = 0

        tableInput.forEachIndexed { rowIndex, rowString ->

            val xInRow = rowString.indicesOf("X")
            xInRow.forEach { columnIndex ->
                val startLocation = Location(rowIndex, columnIndex)

                // This is where all the "M"s are
                val possibleLocations = findInitialDirection(startLocation)
                    .toMutableList()

                val possibleLocationsIterator = possibleLocations.listIterator()
                while (possibleLocationsIterator.hasNext()) {
                    var nextPossibleLocation = possibleLocationsIterator.next()

                    for (letter in listOf('A', 'S')) {
                        val nextLocation = findNextLetterLocation(nextPossibleLocation, letter)
                        if (nextLocation == null) {
                            // This is not a valid location, remove this
                            possibleLocationsIterator.remove()
                            break
                        } else {
                            // Update the location being looked at, for the case
                            // where we still need to look for the "S"
                            val updatedLastLocation = nextPossibleLocation.copy(lastLocation = nextLocation)
                            possibleLocationsIterator.set(updatedLastLocation)
                            nextPossibleLocation = updatedLastLocation
                        }
                    }
                }

                validCount += possibleLocations.size
            }
        }

        return validCount
    }

    fun part2(input: List<String>): Int {
        tableInput.clear()

        // Convert the input to a list of strings
        input.forEach { line ->
            tableInput.add(line)
        }

        var validCount = 0

        // Skip the first and last rows
        for (rowIndex in 1..<tableInput.lastIndex) {

            // Also do not consider the first and last columns
            val aInRow = tableInput[rowIndex].indicesOf("A")
                .filter { it > 0 && it < tableInput[rowIndex].length - 2 }

            // For each "A", find the neighbouring "M"
            for (columnIndex in aInRow) {
                val startLocation = Location(rowIndex, columnIndex)

                // Only take diagonals
                val possibleLocations = findInitialDirection(startLocation)
                    .filter { it.direction in listOf(WordDirection.NorthEast, WordDirection.NorthWest, WordDirection.SouthEast, WordDirection.SouthWest) }
                    .toMutableList()

                // If there's only one M around the "A", that's not valid so stop processing this "A"
                if (possibleLocations.size == 1) continue

                // The "M" should be on the same "side" of the square

                // Each of these locations would have an "M"
                val possibleLocationsIterator = possibleLocations.listIterator()
                while (possibleLocationsIterator.hasNext()) {
                    val location = possibleLocationsIterator.next()

                    // Search the opposite direction for an "S"
                    val sLocation = findNextLetterLocation(
                        possibleLocation = PossibleLocation(
                            direction = location.direction.getOppositeDirection(),
                            lastLocation = Location(rowIndex, columnIndex)
                        ),
                        'S'
                    )

                    if (sLocation == null) {
                        possibleLocationsIterator.remove()
                    }
                }


                if (possibleLocations.size <= 1) continue

                validCount++
            }
        }

        println("Pat 2 Valid count: $validCount")
        return validCount
    }

    // Test if implementation meets criteria from the description:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 18)
    check(part2(testInput) == 9)

    // Read the input from the `src/Day04.txt` file.
    val input = readInput("Day04")
    println("**** Part 1: Count = ${part1(input)}") // 2549
    println("**** Part 2: Count = ${part2(input)}")
}
