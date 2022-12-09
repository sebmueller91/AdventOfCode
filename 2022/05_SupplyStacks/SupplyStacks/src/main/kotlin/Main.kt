import java.io.BufferedReader
import java.io.File

fun main(args: Array<String>) {
    val bufferedReader: BufferedReader = File("..\\input.txt").bufferedReader()
    val inputString = bufferedReader.use { it.readText() }
    val splittedInput = inputString.split("\r\n\r\n")

    val stacks1 = inputToStacks(splittedInput[0])
    val instructions1 = inputToInstructions(splittedInput[1])

    executeAllInstructions(stacks1, instructions1, ::executeInstructionCrateMover9000)
    println(getTopCrates(stacks1))

    val stacks2 = inputToStacks(splittedInput[0])
    val instructions2 = inputToInstructions(splittedInput[1])

    executeAllInstructions(stacks2, instructions2, ::executeInstructionCrateMover9001)
    println(getTopCrates(stacks2))
}

private fun getTopCrates(stacks: List<MutableList<String>>): String {
    var string = ""
    stacks.forEach({
        string += it.last()
    })
    return string
}

private fun executeAllInstructions(
    stacks: List<MutableList<String>>,
    instructions: List<Instruction>,
    executeInstruction: (List<MutableList<String>>, Instruction) -> Unit
) {
    instructions.forEach({
        executeInstruction(stacks, it)
    })
}

private fun executeInstructionCrateMover9000(stacks: List<MutableList<String>>, instruction: Instruction) {
    repeat(instruction.amount) {
        stacks[instruction.to].add(stacks[instruction.from].last())
        stacks[instruction.from].removeLast()
    }
}

private fun executeInstructionCrateMover9001(stacks: List<MutableList<String>>, instruction: Instruction) {
    for (i in instruction.amount downTo 1) {
        val fromIndex = stacks[instruction.from].count()-i
        stacks[instruction.to].add(stacks[instruction.from][fromIndex])
        stacks[instruction.from].removeAt(fromIndex)
    }
}

private fun inputToStacks(input: String): List<MutableList<String>> {
    val lines = input.split("\r\n")
    val numberLines = lines.count() - 1

    val numberStacks: Int = lines[0].count() / 4
    val stacks = mutableListOf<MutableList<String>>()
    for (i in 0..numberStacks) {
        stacks.add(mutableListOf<String>())
    }

    for (lineIndex in numberLines - 1 downTo 0) {
        val line = lines[lineIndex]
        for (stackIndex in 0..numberStacks) {
            val char = line[stackIndex * 4 + 1].toString()
            if (!char.isBlank()) {
                stacks[stackIndex].add(line[stackIndex * 4 + 1].toString())
            }
        }
    }

    return stacks
}

private fun inputToInstructions(input: String): List<Instruction> {
    var lines = input.split("\r\n")
    var list = mutableListOf<Instruction>()

    val regex = "move ([0-9]{1,2}) from ([0-9]) to ([0-9])".toRegex()

    lines.forEach({
        val match = regex.find(it)!!
        val (amount, from, to) = match.destructured
        list.add(Instruction(amount = amount.toInt(), from = from.toInt() - 1, to = to.toInt() - 1))
    })
    return list
}

private fun addElementsToStack(stack: MutableList<String>, vararg elements: String) {
    for (element in elements) {
        stack.add(element)
    }
}

private data class Instruction(
    val from: Int,
    val to: Int,
    val amount: Int
)