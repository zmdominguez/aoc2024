/**
 * Part 1
 *
 * Compact the amphipod's hard drive using the process he requested. What is the resulting filesystem checksum?
 *
 * Part 2
 *
 * Start over, now compacting the amphipod's hard drive using this new method instead. What is the resulting filesystem checksum?
 *
 */

private data class Disk(
    val diskSize: Int,
    var freeSpace: Int,
    val status: DiskStatus,
    val fillStatus: FillStatus,
    val fillers: MutableList<Int> = mutableListOf(),
) {
    override fun toString(): String {
        return "size=$diskSize, status=$status, fillStatus=$fillStatus, freeSpace=$freeSpace, fillers=$fillers"
    }
}

private enum class DiskStatus {
    Default,
    Immovable,
    Moved,
}

private enum class FillStatus {
    Default,
    Unfillable,
    Filled,
}

fun main() {
    fun getDiskMap(input: String): List<List<Int>> {
        return input.chunked(2).map { pair ->
            pair.toCharArray().toList()
                .map { it.toString().toInt() }
        }.map {
            if (it.size == 1) {
                listOf(it[0], 0)
            } else {
                it
            }
        }
    }

    fun getContentString(diskMap: MutableList<Disk>): String {
        val contents = buildString {
            diskMap.forEachIndexed { index, disk ->
                if (disk.status == DiskStatus.Moved) {
                    for (i in 1..disk.diskSize) {
                        append(".")
                    }
                } else {
                    for (i in 1..disk.diskSize) {
                        append("($index)")
                    }
                }

                if (disk.fillStatus == FillStatus.Unfillable) {
                    for (i in 1..disk.freeSpace) {
                        append(".")
                    }
                } else {
                    disk.fillers.forEach {
                        append("($it)")
                    }

                    // It may not be fully filled
                    val remaining = disk.freeSpace - disk.fillers.size
                    if (remaining > 0) {
                        for (i in 1..remaining) {
                            append(".")
                        }
                    }
                }
            }
        }
        return contents
    }

    fun part1(input: List<String>): Long {
        val diskMap = getDiskMap(input[0])
        val compacted = mutableListOf<Int>()

        var idStrings = mutableListOf<Int>()
        diskMap.mapIndexed { index, disk ->
            val count = disk[0]
            for (i in 1..count) {
                idStrings.add(index)
            }
        }

        val iterator = diskMap.listIterator()
        while (iterator.hasNext()) {
            val disk = iterator.next()

            compacted.addAll(idStrings.take(disk[0]))
            idStrings = idStrings.drop(disk[0]).toMutableList()

            val freeSpace = disk[1]
            val freeFill = idStrings.takeLast(freeSpace).reversed()
            compacted.addAll(freeFill)

            idStrings = idStrings.dropLast(freeSpace).toMutableList()
        }

        var checkSum = 0L
        compacted.forEachIndexed { index, c ->
            checkSum += c * index
        }
        return checkSum
    }

    fun part2(input: List<String>): Long {
        val diskMapRaw = getDiskMap(input[0]).map { it.toMutableList() }.toMutableList()
        val diskMap = diskMapRaw.mapIndexed { index, disk ->

            Disk(status = if (index == 0) DiskStatus.Immovable else DiskStatus.Default ,
                fillStatus = if (disk[1] == 0) FillStatus.Unfillable else FillStatus.Default,
                diskSize = disk[0],
                freeSpace = disk[1])
        }.toMutableList()

        val iterator = diskMap.listIterator()
        while (iterator.hasNext()) {
            val index = iterator.nextIndex()
            var disk = iterator.next()

            val freeSpace = disk.freeSpace

            // Check for anything that can fill up the space
            // Anything that is Default, Moved, Immovable can be filled up
            var availableSpace = freeSpace
            if (disk.fillStatus == FillStatus.Unfillable || disk.fillStatus == FillStatus.Filled) continue

            for (lastIndex in diskMap.lastIndex downTo index) {

                // We have reached the end, nothing else fits
                if (lastIndex == index) {
                    val diskUpdated = if (disk.fillers.isEmpty()) {
                        disk.copy(fillStatus = FillStatus.Unfillable)
                    } else {
                        disk.copy(
                            freeSpace = disk.fillers.size + disk.freeSpace,
                            fillStatus = FillStatus.Filled)
                    }
                    iterator.set(diskUpdated)
                    break
                }

                val leftMost = diskMap[lastIndex]
                if (leftMost.status in listOf(DiskStatus.Immovable, DiskStatus.Moved)) continue

                val requiredInsertSize = leftMost.diskSize

                // This index does not fit and has been checked and should not move
                if (requiredInsertSize > availableSpace) continue

                // This index fits and should be filled up
                for (fillCount in 1..requiredInsertSize) {
                    disk.fillers.add(lastIndex)
                }
                diskMap[lastIndex] = leftMost.copy(status = DiskStatus.Moved)

                availableSpace -= requiredInsertSize
                if (availableSpace != 0) {
                    disk.freeSpace = availableSpace
                    iterator.set(disk)
                } else {
                    diskMap[index] = disk.copy(fillStatus = FillStatus.Filled)
                    break
                }
            }
        }

        var checkSum = 0L
        var checkSumIndex = 0L
        diskMap.forEachIndexed { index, disk ->
            if (disk.status == DiskStatus.Moved) {
                checkSumIndex += disk.diskSize
            } else {
                for (i in 1..disk.diskSize) {
                    checkSum += checkSumIndex * index
                    checkSumIndex++
                }
            }

            if (disk.fillStatus == FillStatus.Unfillable) {
                checkSumIndex += disk.freeSpace
            } else {
                disk.fillers.forEach {
                    checkSum += checkSumIndex * it
                    checkSumIndex++
                }

                // It may not be fully filled
                val remaining = disk.freeSpace - disk.fillers.size
                if (remaining > 0) {
                    checkSumIndex += remaining
                }
            }
        }

        return checkSum
    }

    // Test if implementation meets criteria from the description:
    val testInput = readInput("Day09_test")
    check(part1(testInput) == 1928L)
    check(part2(testInput) == 2858L)

    // Read the input from the `src/Day09.txt` file.
    val input = readInput("Day09")
    println("**** Part 1: Checksum = ${part1(input)}") // 6432869891895
    println("**** Part 2: Checksum = ${part2(input)}") // 6467290479134
}
