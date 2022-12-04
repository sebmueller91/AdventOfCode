import java.io.BufferedReader
import java.io.File

fun main(args: Array<String>) {
    val bufferedReader: BufferedReader = File("..\\input.txt").bufferedReader()
    val inputString = bufferedReader.use { it.readText() }

    val formattedInput = formatInput(inputString)

    println(countOverlappingIntervals(formattedInput))
}

private fun countOverlappingIntervals(list: List<Pair<Interval, Interval>>) : Int {
    var count = 0

   list.forEach({
       if (interval1DoesContainInterval2(it.first, it.second)
           || interval1DoesContainInterval2(it.second, it.first)) {
           count++
       }
   })

    return count
}

private fun interval1DoesContainInterval2(interval1: Interval, interval2: Interval) : Boolean {
    return interval1.start <= interval2.start && interval1.end >= interval2.end
}

private fun formatInput(input:String) : List<Pair<Interval, Interval>> {
    var splitByLines = input.split("\r\n")

    var list = mutableListOf<Pair<Interval,Interval>>()
    splitByLines.forEach({line ->
        list.add(getIntervalsForLine(line))
    })
    return list
}

private fun getIntervalsForLine(line: String) : Pair<Interval, Interval> {
    val intervalsAsString = line.split(',')
    return Pair(stringToInterval(intervalsAsString[0]), stringToInterval(intervalsAsString[1]))
}

private fun stringToInterval(string: String) : Interval {
    val boundariesAsString = string.split('-')
    return Interval(boundariesAsString[0].toInt(),boundariesAsString[1].toInt())
}

private data class Interval(
    val start: Int,
    val end: Int
)