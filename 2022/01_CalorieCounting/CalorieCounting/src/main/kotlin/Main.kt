import java.io.BufferedReader
import java.io.File

fun main(args: Array<String>) {
    val bufferedReader: BufferedReader = File("..\\input.txt").bufferedReader()
    val inputString = bufferedReader.use { it.readText() }

    val splittedInput = inputString.split("\r\n\r\n")
    val summedUpList = createSumForEachElve(splittedInput)
    println(summedUpList.maxOrNull() ?: "No value")

    val orderedList = summedUpList.sortedBy{it}
    println(orderedList[orderedList.count()-1] + orderedList[orderedList.count()-2] + orderedList[orderedList.count()-3])
}

fun createSumForEachElve(splittedInput: List<String>) : List<Int> {
    var summedUpList = mutableListOf<Int>()
    splittedInput.forEach(
        {
            summedUpList.add(sumUpCaloriesOfOneElve(it))
        }
    )
    return summedUpList
}

// Sums up a String consisting of multiple lines with an Int in each line
fun sumUpCaloriesOfOneElve(string: String) : Int {
    var sum = 0
    val splitted = string.split("\r\n")
    splitted.forEach(
        {
            sum += it.toInt()
        }
    )

    return sum
}
