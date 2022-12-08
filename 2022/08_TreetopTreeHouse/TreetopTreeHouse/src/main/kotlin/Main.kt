import java.io.BufferedReader
import java.io.File

fun main(args: Array<String>) {
    val bufferedReader: BufferedReader = File("..\\input.txt").bufferedReader()
    val inputString = bufferedReader.use { it.readText() }

    val trees = createArrayOfTrees(inputString)
    val visibleTrees = markTreesAsVisible(trees)
    println(countVisibleTrees(visibleTrees))

    val scores = calculateScenicScores(trees)
    println(getMaxScore(scores))
}

private fun countVisibleTrees(visibleTrees: Array<Array<Boolean>>): Int {
    var count = 0
    visibleTrees.forEach {
        it.forEach {
            if (it == true) {
                count++
            }
        }
    }
    return count
}

private fun getMaxScore(trees: Array<Array<Int>>): Int {
    var max = Int.MIN_VALUE
    trees.forEach {
        it.forEach {
            if (it > max) {
                max = it
            }
        }
    }
    return max
}

private fun calculateScenicScores(trees: Array<Array<Int>>): Array<Array<Int>> {
    val scores = Array(trees.count()) { Array(trees[0].count()) { 0 } }

    val directions = listOf(
        { x: Int, y: Int -> Coordinate(x + 1, y) },
        { x: Int, y: Int -> Coordinate(x - 1, y) },
        { x: Int, y: Int -> Coordinate(x, y + 1) },
        { x: kotlin.Int, y: kotlin.Int -> Coordinate(x, y - 1) })

    val isValidIndex: (x: Int, y: Int) -> Boolean =
        { r, c -> r in 0..trees.count() - 1 && c in 0..trees[0].count() - 1 }

    for (i in 0..trees.count() - 1) {
        for (j in 0..trees[i].count() - 1) {
            var score = 1
            directions.forEach { direction ->
                var curCoordinate = direction(i, j)
                var tmpCount = 0
                while (isValidIndex(curCoordinate.r, curCoordinate.c)
                ) {
                    tmpCount++
                    if (trees[curCoordinate.r][curCoordinate.c] >= trees[i][j]) {
                        break
                    }
                    curCoordinate = direction(curCoordinate.r, curCoordinate.c)
                }
                score *= if (tmpCount > 1) tmpCount else 1
            }
            scores[i][j] = score
        }
    }

    return scores
}

private fun markTreesAsVisible(trees: Array<Array<Int>>): Array<Array<Boolean>> {
    val visibleTrees = Array(trees.count()) { Array(trees[0].count()) { false } }

    val directions = listOf(
        { x: Int, y: Int -> Coordinate(x + 1, y) },
        { x: Int, y: Int -> Coordinate(x - 1, y) },
        { x: Int, y: Int -> Coordinate(x, y + 1) },
        { x: kotlin.Int, y: kotlin.Int -> Coordinate(x, y - 1) })

    val isValidIndex: (x: Int, y: Int) -> Boolean =
        { r, c -> r in 0..trees.count() - 1 && c in 0..trees[0].count() - 1 }

    for (i in 0..trees.count() - 1) {
        for (j in 0..trees[i].count() - 1) {
            var reachedBorder = false
            directions.forEach { direction ->
                var curCoordinate = direction(i, j)
                while (isValidIndex(curCoordinate.r, curCoordinate.c)) {
                    if (trees[curCoordinate.r][curCoordinate.c] >= trees[i][j]) {
                        break
                    }
                    curCoordinate = direction(curCoordinate.r, curCoordinate.c)
                }
                if (!isValidIndex(curCoordinate.r, curCoordinate.c)) {
                    visibleTrees[i][j] = true
                }
            }
        }
    }

    return visibleTrees
}

private fun createArrayOfTrees(input: String): Array<Array<Int>> {
    val lines = input.split("\r\n")
    val rows = lines.count()
    val cols = lines[0].count()
    val array = Array(rows) { Array(cols) { 0 } }

    for (i in 0..lines.count() - 1) {
        for (j in 0..lines[i].count() - 1) {
            array[i][j] = lines[i][j].digitToInt()
        }
    }

    return array
}

private data class Coordinate(
    val r: Int,
    val c: Int
)