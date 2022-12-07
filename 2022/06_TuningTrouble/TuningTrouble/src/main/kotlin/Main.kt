import com.sun.org.apache.xpath.internal.operations.Bool
import java.io.BufferedReader
import java.io.File

fun main(args: Array<String>) {
    val bufferedReader: BufferedReader = File("..\\input.txt").bufferedReader()
    val inputString = bufferedReader.use { it.readText() }
    println(findPositionOfFirstMarker(inputString))
}

private fun findPositionOfFirstMarker(input: String) : Int {
    val charArray = input.toCharArray()
    for (i in (4..input.length)) {
        if (containsDuplicateCharacter(charArray.copyOfRange(i-4,i))) {
            return i
        }
    }
    throw Exception()
}

private fun containsDuplicateCharacter(chars: CharArray) : Boolean {
    chars.forEach {
        if (countOccurencesOfChar(chars, it) > 1) {
            return false
        }
    }
    return true
}

private fun countOccurencesOfChar(chars: CharArray, char: Char) : Int {
    var counter = 0
    chars.forEach {
        if (it.equals(char)) {
            counter++
        }
    }
    return counter
}
