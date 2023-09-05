import java.io.BufferedReader
import java.io.File

fun main(args: Array<String>) {
    val test = true
    val bufferedReader: BufferedReader = File(if (test) "../testInput.txt" else "../input.txt").bufferedReader()
    val inputString = bufferedReader.use { it.readText() }

    val valves = getValvesFromInput(inputString)
    val connectionMatrix = getValveConnectionMatrixFromInput(inputString, valves)

}

private fun getMaximumPressure(valves: List<Valve>, connectionMatrix: Array<Array<Boolean>>): Int {
    return getMaximumPressure(
        valves,
        connectionMatrix,
        curValveIndex = 0,
        minutesRemaining = 30,
        valvesOpen = Array(valves.size) { false })
}

private fun getMaximumPressure(
    valves: List<Valve>,
    connectionMatrix: Array<Array<Boolean>>,
    curValveIndex: Int,
    state: SearchState,
): Int {
    if (state.minutesRemaining <= 0) {
        return 0
    }

    val curScore = state.score +
        if (state.valvesOpen[curValveIndex]) state.minutesRemaining * valves[curValveIndex].flowRate
        else 0
    val newValvesOpen = state.valvesOpen.clone()
    newValvesOpen[curValveIndex] = true

    val maxScore = 0
    connectionMatrix[curValveIndex].forEachIndexed { index, connection ->
        if (connection) {
            val connectionScore = getMaximumPressure(
                valves = valves,
                connectionMatrix = connectionMatrix,
                state = SearchState(
                    minutesRemaining = state.minutesRemaining-2,
                    score = stat
                )
            )
            if (connectionScore > maxScore) {

            }
        }
    }
}

private fun getValvesFromInput(input: String): List<Valve> {
    val valves = mutableListOf<Valve>()
    val lines = input.split("\r\n")
    lines.forEach { line ->
        val (name, flowRate) = "Valve (.*) has flow rate=(.*); tunnel.*".toRegex()
            .find(line)!!.destructured
        valves.add(Valve(name, flowRate.toInt()))
    }
    return valves
}

private fun getValveConnectionMatrixFromInput(input: String, valves: List<Valve>): Array<Array<Boolean>> {
    val matrix = Array(valves.size) { Array(valves.size) { false } }
    val lines = input.split("\r\n")
    lines.forEachIndexed { index, line ->
        val (connections) = "Valve .* has flow rate=.*; tunnels? leads? to valves? (.*)".toRegex()
            .find(line)!!.destructured
        connections.split(',').forEach { connection ->
            val connectedIndex = valves.indexOfFirst { connection.trim() == it.name }
            matrix[index][connectedIndex] = true
            matrix[connectedIndex][index] = true
        }
    }
    return matrix
}

private data class Valve(
    val name: String,
    val flowRate: Int {

}

private class SearchState(
    val minutesRemaining: Int,
    val score: Int,
    val valvesOpen: Array<Boolean>
)