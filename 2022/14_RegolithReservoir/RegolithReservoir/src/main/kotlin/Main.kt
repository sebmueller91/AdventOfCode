import java.io.BufferedReader
import java.io.File

fun main(args: Array<String>) {
    val bufferedReader: BufferedReader = File("../input.txt").bufferedReader()
    val inputString = bufferedReader.use { it.readText() }

    val listOfLines = formatInput(inputString)
    val grid = createGrid(listOfLines, false)
    addSandUntilFull(grid)
    grid.plot()
    println()
    println("Problem 1: ${grid.countSand()}")


    val grid2 = createGrid(listOfLines, true)
    addSandUntilFull(grid2)
    grid2.plot()
    println()
    println("Problem 2: ${grid2.countSand()}")
}

private fun addSandUntilFull(grid: Grid) {
    while (grid.addSand()) {
    }
}

private fun createGrid(lines: List<Line>, addFloor: Boolean): Grid {
    val minRowIndex = lines.minBy { line -> line.minRow }.minRow
    val maxRowIndex = lines.maxBy { line -> line.maxRow }.maxRow
    val minColIndex = lines.minBy { line -> line.minCol }.minCol
    val maxColIndex = lines.maxBy { line -> line.maxCol }.maxCol

    val rowStart = 0
    val rowEnd = maxRowIndex + 4
    val colStart = minColIndex - 150
    val colEnd = maxColIndex + 150

    val rows = rowEnd - rowStart
    val cols = colEnd - colStart

    val updatedLines = lines.toMutableList()
    if (addFloor) {
        val floor = Line(listOf(Coordinate(maxRowIndex + 2, colStart + 1), Coordinate(maxRowIndex + 2, colEnd - 2)))
        updatedLines.add(floor)
    }

    val array = Array(rows) { Array(cols) { Cell.AIR } }
    val grid = Grid(array, rowStart, colStart - 1)

    updatedLines.forEach { line ->
        for (i in 0..line.coordinates.size - 2) {
            var curCoord = line.coordinates[i]
            var endCoord = line.coordinates[i + 1]

            while (true) {
                grid.set(curCoord.row, curCoord.col, Cell.ROCK)
                if (curCoord == endCoord) {
                    break
                }
                curCoord = curCoord.updateTowards(endCoord)
            }
        }
    }

    return grid
}

private fun formatInput(input: String): List<Line> {
    val listOfLines = mutableListOf<Line>()

    val lines = input.split("\n")
    lines.forEach { line ->
        val listOfCoords = mutableListOf<Coordinate>()
        val coords = line.split(" -> ")
        coords.forEach { coord ->
            val points = coord.split(",")
            listOfCoords.add(Coordinate(row = points[1].toInt(), col = points[0].toInt())) // x = col, y = row
        }
        listOfLines.add(Line(listOfCoords))
    }

    return listOfLines
}

private data class Grid(
    val grid: Array<Array<Cell>>,
    val startRow: Int,
    val startCol: Int
) {
    lateinit var sandSource: Coordinate
        private set
    val rows: Int = grid.size
    val cols: Int = grid[0].size

    init {
        sandSource = Coordinate(0, 500)
        set(sandSource.row, sandSource.col, Cell.SOURCE)
    }

    fun get(row: Int, col: Int): Cell {
        return grid[row - startRow][col - startCol]
    }

    fun set(row: Int, col: Int, value: Cell) {
        grid[row - startRow][col - startCol] = value
    }

    // returns true if the sand was placed, false if it fell through
    fun addSand(): Boolean {
        var sandPos = sandSource.copy()
        while (true) {
            if (sandPos.row == rows - 1) {
                return false
            } else if (get(sandPos.row + 1, sandPos.col) == Cell.AIR) {
                sandPos = Coordinate(sandPos.row + 1, sandPos.col)
                continue
            } else if (get(sandPos.row + 1, sandPos.col - 1) == Cell.AIR) {
                sandPos = Coordinate(sandPos.row + 1, sandPos.col - 1)
                continue
            } else if (get(sandPos.row + 1, sandPos.col + 1) == Cell.AIR) {
                sandPos = Coordinate(sandPos.row + 1, sandPos.col + 1)
                continue
            } else if (sandPos == sandSource) {
                set(sandPos.row, sandPos.col, Cell.SAND)
                return false}
            else {
                break
            }
        }
        set(sandPos.row, sandPos.col, Cell.SAND)
        return true
    }

    fun plot() {
        for (i in startRow..startRow + grid.size - 1) {
            print("${i} ")
            for (j in startCol..startCol + grid[0].size - 1) {
                when {
                    get(i, j) == Cell.AIR -> print(".")
                    get(i, j) == Cell.ROCK -> print("#")
                    get(i, j) == Cell.SAND -> print("o")
                    get(i, j) == Cell.SOURCE -> print("+")
                }
            }
            println()
        }
    }

    fun countSand(): Int {
        var sum = 0
        for (i in 0..grid.size - 1) {
            for (j in 0..grid[0].size - 1) {
                sum += if (grid[i][j] == Cell.SAND) 1 else 0
            }
        }
        return sum
    }
}

private enum class Cell {
    AIR, ROCK, SAND, SOURCE
}

private data class Line(
    val coordinates: List<Coordinate>
) {

    val minRow
        get() = getExtreme({ i -> coordinates[i].row }, { x, y -> x < y }, Int.MAX_VALUE)

    val maxRow
        get() = getExtreme({ i -> coordinates[i].row }, { x, y -> x > y }, Int.MIN_VALUE)

    val minCol
        get() = getExtreme({ i -> coordinates[i].col }, { x, y -> x < y }, Int.MAX_VALUE)

    val maxCol
        get() = getExtreme({ i -> coordinates[i].col }, { x, y -> x > y }, Int.MIN_VALUE)


    private fun getExtreme(getValue: (Int) -> Int, compare: (Int, Int) -> Boolean, startValue: Int): Int {
        var extreme = startValue
        for (i in 0..coordinates.size - 1) {
            if (compare(getValue(i), extreme)) {
                extreme = getValue(i)
            }
        }
        return extreme
    }
}


private data class Coordinate(
    val row: Int,
    val col: Int
) {
    fun updateTowards(dest: Coordinate): Coordinate {
        val r = if (dest.row == row) row else {
            row + if (dest.row - row > 0) 1 else -1
        }
        val c = if (dest.col == col) col else {
            col + if (dest.col - col > 0) 1 else -1
        }
        return Coordinate(r, c)
    }
}