import java.io.BufferedReader
import java.io.File

fun main(args: Array<String>) {
    val bufferedReader: BufferedReader = File("../testInput.txt").bufferedReader()
    val inputString = bufferedReader.use { it.readText() }

    val measurements = formatInput(inputString)
    val grid = createGrid(measurements)

    grid.fillNonBeaconCells()
    grid.plot()
    //println(grid.countNonBeaconCells())
}

private fun createGrid(measurements: List<Measurement>) : Grid {
    val rowMin = getMinValue(measurements, {x:Int -> measurements[x].sensorPos.row})
    val colMin = getMinValue(measurements, {x:Int -> measurements[x].sensorPos.col})
    val rowMax = getMaxValue(measurements, {x:Int -> measurements[x].sensorPos.row})
    val colMax = getMaxValue(measurements, {x:Int -> measurements[x].sensorPos.col})

    var rows = rowMax-rowMin+2
    var cols = colMax-colMin+2

    val array = Array (rows) {Array (cols) {Cell()} }
    val grid = Grid(array,rowMin, colMin)

    measurements.forEach {
        grid.set(it.sensorPos.row, it.sensorPos.col, grid.get(it.sensorPos.row, it.sensorPos.col).copy(obj = Object.SENSOR))
        grid.set(it.beaconPos.row, it.beaconPos.col, grid.get(it.beaconPos.row, it.beaconPos.col).copy(obj = Object.BEACON))
    }

    return grid
}

private fun getMinValue(measurements: List<Measurement>, getValue: (Int) -> Int): Int {
    var extreme = Int.MAX_VALUE
    for (i in 0..measurements.size - 1) {
        if (getValue(i) - measurements[i].distance() < extreme) {
            extreme = getValue(i) - measurements[i].distance()
        }
    }
    return extreme
}

private fun getMaxValue(measurements: List<Measurement>, getValue: (Int) -> Int): Int {
    var extreme = Int.MIN_VALUE
    for (i in 0..measurements.size - 1) {
        if (getValue(i) + measurements[i].distance() > extreme) {
            extreme = getValue(i) + measurements[i].distance()
        }
    }
    return extreme
}

private fun formatInput(input: String) : List<Measurement> {
    val splitted = input.split('\n')
    val list = mutableListOf<Measurement>()

    splitted.forEach {
        val (sr, sc, br, bc) = "Sensor at x=(.*), y=(.*): closest beacon is at x=(.*), y=(.*)".toRegex().find(input)!!.destructured
        val sensorPos = Coordinate(sr.toInt(), sc.toInt())
        val beaconPos = Coordinate(br.toInt(), bc.toInt())

        list.add(Measurement(sensorPos, beaconPos))
    }

    return list
}

private data class Measurement(
    val sensorPos: Coordinate,
    val beaconPos: Coordinate
) {
     fun distance() : Int {
         return Math.abs(sensorPos.row-beaconPos.row) + Math.abs(sensorPos.col-beaconPos.col)
     }
}

private data class Coordinate(
    val row: Int,
    val col: Int
)

private data class Grid(
    private val grid: Array<Array<Cell>>,
    private val startRow: Int,
    private val startCol: Int,
    private val rows: Int = grid.size,
    private val cols: Int = grid[0].size
) {
    fun get(row: Int, col: Int) : Cell {
        return grid[row-startRow][col-startCol]
    }

    fun set(row: Int, col: Int, cell: Cell) {
        grid[row-startRow][col-startCol] = cell
    }

    fun plot() {
        TODO("Not yet implemented")
    }

    fun fillNonBeaconCells() {
        TODO("Not yet implemented")
    }
}

private data class Cell(
    val canContainBeacon: Boolean = true,
    val obj: Object = Object.EMPTY
)

private enum class Object {
    BEACON,
    SENSOR,
    EMPTY
}