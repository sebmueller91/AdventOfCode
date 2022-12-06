import java.io.BufferedReader
import java.io.File

fun main(args: Array<String>) {
    val bufferedReader: BufferedReader = File("..\\input.txt").bufferedReader()
    val inputString = bufferedReader.use { it.readText() }
    val splittedInput = inputString.split("\r\n\r\n")

    val stacks = inputToStacks(splittedInput[0])
    val instructions = inputToInstructions(splittedInput[1])

    executeAllInstructions(stacks, instructions)
    println(getTopCrates(stacks))
}

private fun getTopCrates(stacks: List<MutableList<String>>) : String {
    var string = ""
    stacks.forEach({
        string += it.last()
    })
    return string
}

private fun executeAllInstructions(stacks: List<MutableList<String>>, instructions: List<Instruction>) {
    instructions.forEach({
        executeInstruction(stacks, it)
    })
}

private fun executeInstruction(stacks: List<MutableList<String>>, instruction: Instruction) {
    repeat(instruction.amount) {
        stacks[instruction.to].add(stacks[instruction.from].last())
        stacks[instruction.from].removeLast()
    }
}

private fun inputToStacks(input: String) : List<MutableList<String>> {
    val lines = input.split("\r\n")
    val numberLines = lines.count()-1

    val numberStacks:Int = lines[0].count()/4
    val stacks = mutableListOf<MutableList<String>>()
    for(i in 0..numberStacks) {
        stacks.add(mutableListOf<String>())
    }

    for (lineIndex in numberLines-1 downTo 0) {
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

private fun inputToInstructions(input: String) : List<Instruction> {
    var lines = input.split("\r\n")
    var list = mutableListOf<Instruction>()

    val regex = "move ([0-9]{1,2}) from ([0-9]) to ([0-9])".toRegex()

    lines.forEach({
        val match = regex.find(it)!!
            val (amount,from,to) = match.destructured
        list.add(Instruction(amount = amount.toInt(),from = from.toInt()-1,to = to.toInt()-1))
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