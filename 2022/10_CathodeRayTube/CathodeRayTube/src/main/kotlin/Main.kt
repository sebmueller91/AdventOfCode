import java.io.BufferedReader
import java.io.File

private val EVALUATION_POINTS = listOf(20, 60, 100, 140, 180, 220)

fun main(args: Array<String>) {
    val bufferedReader: BufferedReader = File("../input.txt").bufferedReader()
    val inputString = bufferedReader.use { it.readText() }

    val operations = formatInput(inputString)
    println(executeOperations(operations))

    val crt = calculateCRT(operations)
    drawCRT(crt)

}

private fun drawCRT(crt: List<String>) {
    for (i in 0..5) {
        for (j in 0..39) {
            print(crt[i*40+j])
        }
        println()
    }
}

private fun calculateCRT(operations: List<Operation>) : List<String> {
    val crt = mutableListOf<String>()
    var x = 1
    var cycleCount = 1
    var operationIndex = 0
    var drawIndex = 0
    updateCRT(crt, cycleCount, x)
    while (operationIndex < operations.count()-1) {
        if (operations[operationIndex].operationType == OperationType.NOOP) {
            cycleCount++
            operationIndex++
        } else if (operations[operationIndex].operationType == OperationType.ADDX) {
            cycleCount++
            updateCRT(crt, cycleCount, x)
            cycleCount++
            x += operations[operationIndex].argument!!
            operationIndex++
        }
        updateCRT(crt, cycleCount, x)
    }
    return crt
}

private fun updateCRT(crt: MutableList<String>, cycleCount: Int, x: Int) {
    val drawIndex = (cycleCount-1) % 40
    crt.add(getCharToDraw(drawIndex, x))
}

private fun getCharToDraw(drawIndex: Int, x: Int) : String {
    return if (drawIndex in x-1..x+1) "#" else "."
}

private fun executeOperations(operations: List<Operation>) : Int {
    var x = 1
    var cycleCount = 1
    var operationIndex = 0
    var sumOfSignalStrengths = 0

    while (true) {
        if (cycleCount > EVALUATION_POINTS.max()) {
            break
        }

        if (operations[operationIndex].operationType == OperationType.NOOP) {
            cycleCount++
            operationIndex++
        } else if (operations[operationIndex].operationType == OperationType.ADDX) {
            cycleCount++
            sumOfSignalStrengths = evaluateSignalStrengths(cycleCount, x, sumOfSignalStrengths)
            cycleCount++
            x += operations[operationIndex].argument!!
            operationIndex++
        }

        sumOfSignalStrengths = evaluateSignalStrengths(cycleCount, x, sumOfSignalStrengths)

    }
    return sumOfSignalStrengths
}

private fun evaluateSignalStrengths(cycle: Int, x: Int, sumOfSignalStrengths: Int) : Int {
    if (EVALUATION_POINTS.contains(cycle)) {
        return sumOfSignalStrengths + cycle * x
    }
    return sumOfSignalStrengths
}

private fun formatInput(input: String) : List<Operation> {
    val operationList = mutableListOf<Operation>()

    val lines = input.split("\n")
    lines.forEach {line ->
        val arguments = line.split(" ")
        when {
            arguments[0].equals("noop") -> operationList.add(Operation(OperationType.NOOP))
            arguments[0].equals("addx") -> operationList.add(Operation(OperationType.ADDX, arguments[1].toInt()))
            else -> {}
        }
    }

    return operationList
}

private class Operation(
    val operationType: OperationType,
    val argument: Int? = null,
    val startCycle: Int? = null
)

private enum class OperationType(cycles: Int) {
    NOOP(cycles = 1),
    ADDX(cycles = 2)
}