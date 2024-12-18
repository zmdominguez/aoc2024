/**
 * Part 1
 *
 * How many distinct positions will the guard visit before leaving the mapped area?
 *
 * Part 2
 *
 * You need to get the guard stuck in a loop by adding a single new obstruction.
 * How many different positions could you choose for this obstruction?
 *
 */

private const val START_MARKER = "^"
private const val OBSTACLE_MARKER = "#"
private const val TRAVERSED_MARKER = "X"

private sealed class GuardDirection {
    data object Up : GuardDirection() {
        override fun getNextDirection() = Right
        override fun getPath(input: List<String>, location: Location) = input.getStringAbove(location)
    }

    data object Down : GuardDirection() {
        override fun getNextDirection() = Left
        override fun getPath(input: List<String>, location: Location) = input.getStringBelow(location)
    }

    data object Left : GuardDirection() {
        override fun getNextDirection() = Up
        override fun getPath(input: List<String>, location: Location) = input.getStringToLeftOf(location)
    }
    data object Right : GuardDirection() {
        override fun getNextDirection() = Down
        override fun getPath(input: List<String>, location: Location) = input.getStringToRightOf(location)
    }

    abstract fun getNextDirection(): GuardDirection
    abstract fun getPath(input: List<String>, location: Location): String
}

private fun GuardPath.getNextPathEnd(roomMap: List<String>): Location {
    val guardDirection = this.direction
    val startLocation = this.location

    val path = guardDirection.getPath(roomMap, startLocation)

    val endLocation = when (guardDirection) {
        GuardDirection.Down -> {
            val obstacleIndex = path.indexOf(OBSTACLE_MARKER)
            val endIndex = if (obstacleIndex < 0) roomMap.lastIndex else startLocation.row + obstacleIndex
            Location(endIndex, startLocation.column)
        }
        GuardDirection.Left -> {
            val obstacleIndex = path.lastIndexOf(OBSTACLE_MARKER)
            val endIndex = if (obstacleIndex < 0) 0 else obstacleIndex + 1
            Location(startLocation.row, endIndex)
        }
        GuardDirection.Right -> {
            val obstacleIndex = path.indexOf(OBSTACLE_MARKER)
            val endIndex = if (obstacleIndex < 0) roomMap[startLocation.row].lastIndex else startLocation.column + obstacleIndex
            Location(startLocation.row, endIndex)
        }
        GuardDirection.Up -> {
            val obstacleIndex = path.lastIndexOf(OBSTACLE_MARKER)
            val endIndex = if (obstacleIndex < 0) 0 else obstacleIndex + 1
            Location(endIndex, startLocation.column)
        }
    }

//    println("Next end location: $endLocation")
    return endLocation
}

private data class GuardPath(val direction: GuardDirection, val location: Location)

private fun MutableList<String>.markTraversedPaths(startLocation: Location, endLocation: Location) {
    var currentPath = ""

    if (startLocation.column == endLocation.column) {
        val start = if (startLocation.row < endLocation.row) startLocation.row else endLocation.row
        val end = if (startLocation.row > endLocation.row) startLocation.row else endLocation.row
        for (i in start..end) {
            currentPath = this[i].replaceRange(startLocation.column..endLocation.column, TRAVERSED_MARKER)
            this[i] = currentPath
        }
    } else if (startLocation.row == endLocation.row) {
        val start = if (startLocation.column < endLocation.column) startLocation.column else endLocation.column
        val end = if (startLocation.column > endLocation.column) startLocation.column else endLocation.column
        currentPath = this[startLocation.row]
        for (i in start..end) {
            currentPath = currentPath.replaceRange(i..i, TRAVERSED_MARKER)
        }
        this[endLocation.row] = currentPath
    }
}

fun main() {
    val roomMap = mutableListOf<String>()

    fun findStartLocation(): Location {
        roomMap.forEachIndexed { index, line ->
            val startIndex = line.indexOf(START_MARKER)
            if (startIndex >= 0) {
                return Location(row = index, column = startIndex)
            }
        }
        throw IllegalStateException("Should not happen")
    }

    fun part1(input: List<String>): Int {
        roomMap.clear()
        roomMap.addAll(input)
        val traversedMap = roomMap.toMutableList()

        val startLocation = findStartLocation()
        var guardPath = GuardPath(direction = GuardDirection.Up, location = startLocation)
        var hasExited = false

        while (!hasExited) {
            val nextObstacleLocation = guardPath.getNextPathEnd(roomMap)

            val nextGuardPath = GuardPath(
                direction = guardPath.direction.getNextDirection(),
                location = nextObstacleLocation
            )

            traversedMap.markTraversedPaths(guardPath.location, nextGuardPath.location)

            guardPath = nextGuardPath

            if (guardPath.location.row == 0
                || guardPath.location.row == traversedMap.lastIndex
                || guardPath.location.column == 0
                || guardPath.location.column == traversedMap[0].lastIndex) {
                hasExited = true
            }
        }

        var traversedPaths = 0
        traversedMap.forEach { line ->
            traversedPaths += line.count { it.toString() == TRAVERSED_MARKER }
        }

        return traversedPaths
    }

    fun part2(input: List<String>): Int {
        roomMap.clear()
        roomMap.addAll(input)

        return input.size
    }

    // Test if implementation meets criteria from the description:
    val testInput = readInput("Day06_test")
    check(part1(testInput) == 41)
//    check(part2(testInput) == 1)

    // Read the input from the `src/Day06.txt` file.
    val input = readInput("Day06")
    println("**** Part 1: Count = ${part1(input)}") // 4663
//    println("**** Part 2: Count = ${part2(input)}")

}

