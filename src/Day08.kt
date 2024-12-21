/**
 * Part 1
 *
 * How many unique locations within the bounds of the map contain an antinode?
 *
 * Part 2
 *
 * Calculate the impact of the signal using this updated model.
 * How many unique locations within the bounds of the map contain an antinode?
 *
 */

private data class Antenna(
    val antennaSymbol: Char,
    val location: Location,
)

fun main() {
    var tableWidth = 0
    var tableHeight = 0

    fun findAntennaCoordinates(input: List<String>): List<Pair<Char, List<Antenna>>> {
        val allAntennas = mutableListOf<Antenna>()

        val antennaMap = input.toList()

        antennaMap.forEachIndexed { row, line ->
            line.forEachIndexed { column, c ->
                if (c != '.') {
                    allAntennas.add(Antenna(c, Location(row, column)))
                }
            }
        }

        tableWidth = antennaMap[0].lastIndex
        tableHeight = antennaMap.size - 1

        return allAntennas.groupBy { it.antennaSymbol }.toList()
    }

    fun isWithinBounds(row: Int, column: Int): Boolean = column in 0..tableWidth && row in 0..tableHeight

    fun findAllValidFrom(isExhaustive: Boolean = false,
                         location: Location,
                         rowDistance: Int,
                         columnDistance: Int,
                         lookAbove: Boolean = false): List<Location> {
        val validCoords = mutableSetOf<Location>()

        var loopCount = if (lookAbove) 1 else -1
        var exhausted = false

        while (!exhausted) {
            if (isExhaustive) {
                validCoords.add(location)
            } else {
                exhausted = true
            }

            val antiNodeCol = location.column + (columnDistance * loopCount)
            val antiNodeRow = location.row + (rowDistance * loopCount)

            if (isWithinBounds(row = antiNodeRow, column = antiNodeCol)) {
                validCoords.add(Location(row = antiNodeRow, column = antiNodeCol))
            } else {
                exhausted = true
            }
            if (lookAbove) loopCount++ else loopCount--
        }

        return validCoords.toList()
    }

    fun findCoordsForSymbol(locations: List<Location>,
                            isExhaustive: Boolean = false): List<Location> {
        val validCoords = mutableSetOf<Location>()

        locations.forEachIndexed { index, firstLocation ->
            // Iterate through all other locations
            for (nextLocation in index + 1 until locations.size) {
                val secondLocation = locations[nextLocation]

                // If this is negative, second is to the right
                val colDistance = firstLocation.column - secondLocation.column

                // If this is negative, second is below
                val rowDistance = firstLocation.row - secondLocation.row

                // Find all coords above the first
                validCoords.addAll(findAllValidFrom(location = firstLocation, isExhaustive = isExhaustive,
                    columnDistance = colDistance, rowDistance = rowDistance,
                    lookAbove = true))

                // Find all coords below the second
                validCoords.addAll(findAllValidFrom(location = secondLocation, isExhaustive = isExhaustive,
                    columnDistance = colDistance, rowDistance = rowDistance,
                    lookAbove = false))
            }
        }

        return validCoords.toList()
    }

    fun part1(input: List<String>): Int {

        val validCoords = mutableSetOf<Location>()

        findAntennaCoordinates(input).forEach { symbol ->
            validCoords.addAll(findCoordsForSymbol(symbol.second.map { it.location }))
        }

        return validCoords.size
    }

    fun part2(input: List<String>): Int {
        val validCoords = mutableSetOf<Location>()

        findAntennaCoordinates(input).forEach { symbol ->
            val coordsForSymbol = findCoordsForSymbol(
                symbol.second.map { it.location }.toList(),
                isExhaustive = true).toSet()

            validCoords.addAll(coordsForSymbol)
        }

        return validCoords.size
    }

    // Test if implementation meets criteria from the description:
    val testInput = readInput("Day08_test")
    check(part1(testInput) == 14)
    check(part2(testInput) == 34)

    // Read the input from the `src/Day08.txt` file.
    val input = readInput("Day08")
    println("**** Part 1: Count = ${part1(input)}") // 394
    println("**** Part 2: Count = ${part2(input)}") // 1277
}
