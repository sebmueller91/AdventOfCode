import java.io.BufferedReader
import java.io.File

fun main() {
    val bufferedReader: BufferedReader = File("../input.txt").bufferedReader()
    val inputString = bufferedReader.use { it.readText() }

    val instructions = parseInput(inputString)
    val rope = Rope(10)
    executeInstructions(rope, instructions)
    println(rope.getNumberOfTraversedCoords())
    //rope.printTraversedCoordsGrid()
}

private fun executeInstructions(rope: Rope, instructions: List<Instruction>) {
    instructions.forEach {
        rope.applyInstruction(it)
    }
}

private fun parseInput(input: String): List<Instruction> {
    val instructions = mutableListOf<Instruction>()
    input.split("\n").forEach {
        val direction = when {
            it[0] == 'L' -> Direction.LEFT
            it[0] == 'U' -> Direction.UP
            it[0] == 'R' -> Direction.RIGHT
            else -> Direction.DOWN
        }
        instructions.add(Instruction(direction, it.substring(2,it.count()).toInt()))
    }
    return instructions
}

private class Rope(
    var knots: Int
) {
    private lateinit var head: Coord
    private lateinit var ropeElements: MutableList<Coord>

    init {
        head = Coord(0,0)
        ropeElements = mutableListOf<Coord>()
        ropeElements.add(head)
        for (i in 1..knots-1) {
            ropeElements.add(Coord(0,0))
        }
    }
    private val traversedCoords = mutableListOf(Coord(0,0))

    fun printTraversedCoordsGrid() {
        val rowMin = traversedCoords.minBy{it.row}.row
        val rowMax = traversedCoords.maxBy{it.row}.row
        val colMin = traversedCoords.minBy{it.col}.col
        val colMax = traversedCoords.maxBy{it.col}.col

        for (i in rowMax+1 downTo rowMin-1) {
            for (j in colMin-1..colMax+1) {
                if (i == 0 && j == 0) {
                    print("s")
                } else {
                    print("${if (traversedCoords.contains(Coord(i, j))) "#" else "."}")
                }

            }
            println()
        }
        println()
    }

    fun getNumberOfTraversedCoords(): Int {
        return traversedCoords.count()
    }

    fun applyInstruction(instruction: Instruction) {
        for (i in 0..instruction.steps - 1) {
            head.move(instruction.direction)
            for (i in 1..ropeElements.count()-1) {
                if (!knotsAreAdjacent(ropeElements[i-1],ropeElements[i])) {
                    followUpNextKnot(ropeElements[i-1],ropeElements[i])
                    if (i == ropeElements.count()-1
                        && ropeElements[i] !in traversedCoords) {
                        traversedCoords.add(ropeElements[i].copy())
                    }
                }
            }
//            if (!knotsAreAdjacent()) {
//                makeTailFollow()
//                if (isLastKnot && knot !in traversedCoords) {
//                    traversedCoords.add(knot.copy())
//                }
//            }
        }
    }

    private fun followUpNextKnot(previousKnot: Coord, knot: Coord) {
        val offset = offsetHeadTail(previousKnot, knot)
        if (offset.col == 0) {
            knot.row += if (offset.row > 0) 1 else -1
        } else if (offset.row == 0) {
            knot.col += if (offset.col > 0) 1 else -1
        } else {
            knot.row += if (offset.row > 0) 1 else -1
            knot.col += if (offset.col > 0) 1 else -1
        }
    }

    private fun knotsAreAdjacent(knot1: Coord, knot2: Coord): Boolean {
        val offset = offsetHeadTail(knot1, knot2)
        return Math.abs(offset.row) <= 1 && Math.abs(offset.col) <= 1
    }

    private fun offsetHeadTail(knot1: Coord, knot2: Coord): Coord {
        return Coord(knot1.row - knot2.row, knot1.col - knot2.col)
    }
}

private data class Coord(
    var row: Int,
    var col: Int
) {
    fun move(direction: Direction) {
        when (direction) {
            Direction.LEFT -> col--
            Direction.UP -> row++
            Direction.RIGHT -> col++
            else -> row--
        }
    }
}

private data class Instruction(
    val direction: Direction,
    val steps: Int
)

private enum class Direction {
    LEFT, UP, RIGHT, DOWN
}