import java.io.BufferedReader
import java.io.File

private val EVALUATION_POINTS = listOf(20, 60, 100, 140, 180, 220)

fun main(args: Array<String>) {
    val bufferedReader: BufferedReader = File("../input.txt").bufferedReader()
    val inputString = bufferedReader.use { it.readText() }

    val operations = formatInput(inputString)


    println(executeOperations(operations))
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
            sumOfSignalStrengths = checkEvaluation(cycleCount, x, sumOfSignalStrengths)
            cycleCount++
            x += operations[operationIndex].argument!!
            operationIndex++
        }

        sumOfSignalStrengths = checkEvaluation(cycleCount, x, sumOfSignalStrengths)

    }
    return sumOfSignalStrengths
}

private fun checkEvaluation(cycle: Int, x: Int, sumOfSignalStrengths: Int) : Int {
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
    val argument: Int? = null
)

private enum class OperationType(cycles: Int) {
    NOOP(cycles = 1),
    ADDX(cycles = 2)
}