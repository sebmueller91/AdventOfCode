import java.io.BufferedReader
import java.io.File

fun main(args: Array<String>) {
    val bufferedReader: BufferedReader = File("..\\input.txt").bufferedReader()
    val inputString = bufferedReader.use { it.readText() }

    val list = convertToList(inputString)
    var score = sumUpRounds(list)
    println(score)

    val list2 = convertToListOfOutcomes(inputString)
    val list3 = createListAccordingToOutcomes(list2)
    println(sumUpRounds(list3))
}

private fun createListAccordingToOutcomes(inputList: List<Pair<Type,Outcome>>) : List<Pair<Type,Type>> {
    val list = mutableListOf<Pair<Type, Type>>()
    inputList.forEach({
         val type = if (it.second == Outcome.LOOSE)
            getLoosingType(it.first)
            else if (it.second == Outcome.DRAW) it.first
            else getDefeatingType(it.first)
        list.add(Pair(it.first, type))
    })
    return list
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

private fun getLoosingType(type: Type): Type {
    return when (type) {
        Type.ROCK -> Type.SCISSORS
        Type.PAPER -> Type.ROCK
        else -> Type.PAPER
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

private fun convertToListOfOutcomes(input: String): List<Pair<Type, Outcome>> {
    val rounds = input.split("\r\n")
    var list = mutableListOf<Pair<Type, Outcome>>()
    rounds.forEach({
        val movesAsString = it.split(' ')
        val pair = Pair(convertStringToType(movesAsString[0]), convertStringToOutcome(movesAsString[1]))
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

private fun convertStringToOutcome(string: String): Outcome {
    if (string.equals("X")) {
        return Outcome.LOOSE
    } else if (string.equals("Y")) {
        return Outcome.DRAW
    } else {
        return Outcome.WIN
    }
}

private enum class Type {
    ROCK, PAPER, SCISSORS
}

private enum class Outcome {
    LOOSE, DRAW, WIN
}
