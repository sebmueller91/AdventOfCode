import java.io.BufferedReader
import java.io.File
import java.lang.Exception

fun main(args: Array<String>) {
    val test = false
    val bufferedReader: BufferedReader = File(if (test) "../testInput.txt" else "../input.txt").bufferedReader()
    val inputString = bufferedReader.use { it.readText() }
    val xyMax = if (test) 20 else 4000000

    val measurements = formatInput(inputString)
    val nonBeaconCoords = getListsOfNonBeaconCoordsInRow(if (test) 10 else 2000000, measurements)
    val combinedList = combineLists(nonBeaconCoords)
    println("Problem 1: ${combinedList.size}")
    if (test) plot(measurements)

    val beaconPos = findPossibleBeaconLocation(measurements, xyMax)
    println("Problem 2: ${beaconPos}, tuning frequency: ${calculateTuningFrequency(beaconPos)}")
}

private fun findPossibleBeaconLocation(measurements: List<Measurement>, xyMax: Int): Coordinate {
    val block = Block(
        leftTop = Coordinate(0, 0),
        rightBottom = Coordinate(xyMax, xyMax),
    )

    var res = block.findBeacon(measurements)
    if (res == null) {
        throw Exception("No beacon found")
    } else {
        return res
    }
}

private fun Block.findBeacon(measurements: List<Measurement>): Coordinate? {
    if (size == 1) {
        return leftTop
    }
    else {
        val subBlocks = divideIntoSubblocks()
        subBlocks.forEach {
            if (!it.anySensorCoversBlock(measurements)) {
                val res = it.findBeacon(measurements)
                if (res != null && !res.isCovered(measurements)) {
                    return res
                }
            }
        }
    }
    return null
}

private fun Coordinate.isCovered(measurements: List<Measurement>) : Boolean {
    val nonBeaconCoords = getListsOfNonBeaconCoordsInRow(this.y, measurements)
    val combinedList = combineLists(nonBeaconCoords)
    return combinedList.contains(this)
}

private fun Block.anySensorCoversBlock(measurements: List<Measurement>) : Boolean{
    return measurements.any{
        leftTop.coordinateIsInReachOfSensor(it)
                && rightTop.coordinateIsInReachOfSensor(it)
                && rightBottom.coordinateIsInReachOfSensor(it)
                && leftBottom.coordinateIsInReachOfSensor(it)
    }

}
private fun Coordinate.coordinateIsInReachOfSensor(measurement: Measurement): Boolean {
    return manhattanDistance(measurement.sensorPos) <= measurement.distanceSensorBeacon()
}

private fun calculateTuningFrequency(coordinate: Coordinate): Long {
    return coordinate.x * 4000000L + coordinate.y
}

private fun plot(measurements: List<Measurement>) {
    for (r in -5..26) {
        val nonBeaconCoords = getListsOfNonBeaconCoordsInRow(r, measurements)
        val combinedList = combineLists(nonBeaconCoords)
        print(r.toString().padStart(2, ' '))
        for (c in -5..25) {
            val curCoord = Coordinate(c, r)
            when {
                combinedList.contains(Coordinate(c, r)) -> print('#')
                measurements.any { it.sensorPos == curCoord } -> print('S')
                measurements.any { it.beaconPos == curCoord } -> print('B')
                else -> print('.')
            }
        }
        println()
    }
}

private fun combineLists(lists: List<List<Coordinate>>): List<Coordinate> {
    var combinedList = lists.first()
    lists.drop(1).forEach { list ->
        combinedList = (combinedList + list).distinct()
    }
    return combinedList
}

private fun getListsOfNonBeaconCoordsInRow(
    row: Int,
    measurements: List<Measurement>,
    ignoreBeacons: Boolean = false
): List<List<Coordinate>> {
    val lists = mutableListOf<List<Coordinate>>()
    measurements.forEach { measurement ->
        val curList = mutableListOf<Coordinate>()

        val dist = measurement.distanceSensorBeacon()
        for (i in measurement.sensorPos.x - dist..measurement.sensorPos.x + dist) {
            val curCoord = Coordinate(i, row)
            if (curCoord.manhattanDistance(measurement.sensorPos) <= dist
                && (ignoreBeacons || !measurements.any { it.beaconPos == curCoord })
            ) {
                curList.add(curCoord)
            }
        }

        lists.add(curList)
    }
    return lists
}

private fun formatInput(input: String): List<Measurement> {
    val splitted = input.split('\n')
    val list = mutableListOf<Measurement>()

    splitted.forEach {
        val (sx, sy, bx, by) = "Sensor at x=(.*), y=(.*): closest beacon is at x=(.*), y=(.*)".toRegex()
            .find(it)!!.destructured
        val sensorPos = Coordinate(sx.toInt(), sy.toInt())
        val beaconPos = Coordinate(bx.toInt(), by.toInt())

        list.add(Measurement(sensorPos, beaconPos))
    }

    return list
}

private data class Measurement(
    val sensorPos: Coordinate,
    val beaconPos: Coordinate
) {
    fun distanceSensorBeacon(): Int {
        return sensorPos.manhattanDistance(beaconPos)
    }
}

private data class Block(
    val leftTop: Coordinate,
    val rightBottom: Coordinate

) {
    val size:Int
        get() = rightBottom.x - leftTop.x
    val half_size: Int
        get() = size/2
    val leftBottom: Coordinate = Coordinate(leftTop.x, rightBottom.y)
    val rightTop: Coordinate = Coordinate(rightBottom.x, leftTop.y)

    fun divideIntoSubblocks(): List<Block> {
        return listOf(
            // top left block
            Block(
                leftTop = leftTop,
                rightBottom = Coordinate(leftTop.x + half_size, leftTop.y + half_size)
            ),
            // top right block
            Block(
                leftTop = Coordinate(leftTop.x+half_size, leftTop.y),
                rightBottom = Coordinate(rightBottom.x, leftTop.y+half_size)
            ),
            // bottom right block
            Block(
                leftTop = Coordinate(leftTop.x+half_size, leftTop.y+half_size),
                rightBottom = rightBottom
            ),
            // bottom left block
            Block(
                leftTop = Coordinate(leftTop.x, leftTop.y+half_size),
                rightBottom = Coordinate(leftTop.x+half_size, rightBottom.y)
            )
        )

    }
}

private data class Coordinate(
    val x: Int,
    val y: Int
) {
    operator fun plus(other: Coordinate): Coordinate {
        return Coordinate(x + other.x, y + other.y)
    }

    fun manhattanDistance(other: Coordinate): Int {
        return Math.abs(x - other.x) + Math.abs(y - other.y)
    }
}