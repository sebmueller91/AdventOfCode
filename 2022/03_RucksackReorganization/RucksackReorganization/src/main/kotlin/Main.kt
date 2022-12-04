import java.io.BufferedReader
import java.io.File
import java.lang.Exception

fun main(args: Array<String>) {
    val bufferedReader: BufferedReader = File("..\\input.txt").bufferedReader()
    val inputString = bufferedReader.use { it.readText() }

    val formattedInput = formatInput(inputString)

    println(sumUpPriorities(formattedInput))
}

private fun sumUpPriorities(input: List<Pair<String, String>>) : Int {
    var score = 0
    input.forEach({
        var duplicateChar = getDuplicateCharacter(it)
        score += getPriorityOfCharacter(duplicateChar)
    })
    return score
}
private fun getPriorityOfCharacter(char: Char) : Int {
    var charAsInt = char.code.toInt()
    when (charAsInt) {
        // a-z have value 97-122
        in (97..122) -> return charAsInt -96
        // A-Z have value 65-90
        else -> return charAsInt -38
    }
}

private fun getDuplicateCharacter(pair: Pair<String, String>) : Char {
    pair.first.toCharArray().forEach {f ->
        pair.second.toCharArray().forEach {s ->
            if (f.equals(s)) {
                return f
            }
        }
    }
    throw Exception("No duplicate character in first ${pair.first} and second ${pair.second}")
}

private fun formatInput(input: String) : List<Pair<String, String>> {
    val splittedByLines = input.split("\r\n")
    var retValue = mutableListOf<Pair<String, String>>()
    splittedByLines.forEach({
        retValue.add(splitStringInHalf(it))
    })
    return retValue
}

private fun splitStringInHalf(string: String) : Pair<String, String> {
    val len = string.length/2
    return Pair<String, String>(string.substring(0,len),string.substring(len,string.length))
}