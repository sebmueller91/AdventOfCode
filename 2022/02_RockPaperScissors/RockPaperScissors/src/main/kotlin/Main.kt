import java.io.BufferedReader
import java.io.File

fun main(args: Array<String>) {
    val bufferedReader: BufferedReader = File("..\\input.txt").bufferedReader()
    val inputString = bufferedReader.use { it.readText() }

    val list = convertToList(inputString)
    var score = sumUpRounds(list)
    
    println("Final Score: ${score}")
}

private fun sumUpRounds(listofRounds: List<Pair<Type, Type>>) : Int {
    var score = 0

    listofRounds.forEach(
        {
            score += calculatScoreForRound(it.first, it.second)
        }
    )

    return score
}

private fun calculatScoreForRound(othersType: Type, myType: Type): Int {
    var score = 0
    score += if (myType == Type.ROCK) 1 else if (myType == Type.PAPER) 2 else 3

    if (myType == othersType) {
        score += 3
    } else if (myType == getDefeatingType(othersType)) {
        score += 6
    }

    return score
}

private fun getDefeatingType(type: Type): Type {
    return when (type) {
        Type.ROCK -> Type.PAPER
        Type.PAPER -> Type.SCISSORS
        else -> Type.ROCK
    }
}

private fun convertToList(input: String): List<Pair<Type, Type>> {
    val rounds = input.split("\r\n")
    var list = mutableListOf<Pair<Type, Type>>()
    rounds.forEach({
        val movesAsString = it.split(' ')
        val pair = Pair(convertStringToType(movesAsString[0]), convertStringToType(movesAsString[1]))
        list.add(pair)
    })
    return list
}

private fun convertStringToType(string: String): Type {
    if (string.equals("A") || string.equals("X")) {
        return Type.ROCK
    } else if (string.equals("B") || string.equals("Y")) {
        return Type.PAPER
    } else {
        return Type.SCISSORS
    }
}

private enum class Type {
    ROCK, PAPER, SCISSORS
}
