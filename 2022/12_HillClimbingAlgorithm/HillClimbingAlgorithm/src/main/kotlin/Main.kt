import java.io.BufferedReader
import java.io.File

fun main(args: Array<String>) {
    val bufferedReader: BufferedReader = File("../input.txt").bufferedReader()
    val inputString = bufferedReader.use { it.readText() }

    val map = formatInput(inputString)
    map.calculateShortestWays()

    println(map.shortestWays[map.start.row][map.start.col])
    println(getBestBikeWayLength(map))
}

private fun getBestBikeWayLength(map: Map) : Int {
    var min = Int.MAX_VALUE
    for (i in 0..map.rows-1) {
        for (j in 0..map.cols-1) {
            if (map.heights[i][j] == charToHeight('a')) {
                min = Math.min(map.shortestWays[i][j], min)
            }
        }
    }
    return min
}

private fun formatInput(input: String): Map {
    val lines = input.split("\n")
    val rows = lines.count()
    val cols = lines[0].count()
    val array = Array(rows) { Array(cols) { 0 } }
    var start = Coord(0, 0)
    var dest = Coord(0, 0)

    for (i in 0..rows - 1) {
        for (j in 0..cols - 1) {
            array[i][j] = when {
                lines[i][j] == 'S' -> {
                    start = Coord(i, j)
                    charToHeight('a')
                }
                lines[i][j] == 'E' -> {
                    dest = Coord(i, j)
                    charToHeight('z')
                }
                else -> charToHeight(lines[i][j])
            }
        }
    }

    return Map(array, start, dest)
}

private fun printArray(array: Array<Array<Int>>) {
    for (i in 0..array.size -1) {
        for (j in 0..array[i].size-1) {
            print("${array[i][j]} ")
        }
        println()
    }
}

private fun charToHeight(c: Char): Int {
    return c.code - 96
}

private class Map(
    val heights: Array<Array<Int>>,
    val start: Coord,
    val destination: Coord,
    val shortestWays: Array<Array<Int>> = Array(heights.size) { Array(heights[0].size) { Int.MAX_VALUE } }
) {
    val rows
        get() = heights.size
    val cols
        get() = heights[0].size

    fun calculateShortestWays() {
        fillField(destination, 0)
    }

    private fun fillField(pos: Coord, stepsNeeded: Int) {
        val curHeight = heights[pos.row][pos.col]
        val curShortestWay = shortestWays[pos.row][pos.col]

        if (curShortestWay <= stepsNeeded) {
            return
        }
        shortestWays[pos.row][pos.col] = stepsNeeded

        val validNeighbors = getValidNeighbors(pos)
        validNeighbors.forEach {
            val height = heights[it.row][it.col]
            if (height >= curHeight-1) {
                fillField(it, stepsNeeded+1)
            }
        }

    }

    private fun getValidNeighbors(pos: Coord): List<Coord> {
        val neighbors = mutableListOf(
            Coord(pos.row - 1, pos.col),
            Coord(pos.row, pos.col - 1),
            Coord(pos.row + 1, pos.col),
            Coord(pos.row, pos.col + 1)
        )
        val filteredNeighmors = mutableListOf<Coord>()
        neighbors.forEach {
            if (it.row in 0..rows - 1 && it.col in 0..cols - 1) {
                filteredNeighmors.add(it)
            }
        }
        return filteredNeighmors
    }
}

private data class Coord(
    val row: Int,
    val col: Int
)
