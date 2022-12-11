import java.io.BufferedReader
import java.io.File
import java.lang.Exception

fun main(args: Array<String>) {
    val bufferedReader: BufferedReader = File("../input.txt").bufferedReader()
    val inputString = bufferedReader.use { it.readText() }

    val monkeys = formatInput(inputString)
    executeRounds(monkeys, 20)
    println(getMutiplyOfMax2(monkeys))

    val monkeys2 = formatInput(inputString)
    val divisorProduct = getDivisorProduct(monkeys2)
    executeRounds(monkeys2, 10000, false, divisorProduct)
    println(getMutiplyOfMax2(monkeys2))
}

private fun getDivisorProduct(monkeys: List<Monkey>): Long {
    var product = 1L
    monkeys.forEach {
        product *= it.testDivisor
    }
    return product
}

private fun getMutiplyOfMax2(monkeys: List<Monkey>): Long {
    val list = mutableListOf<Long>()
    monkeys.forEach {
        list.add(it.inspectedItems)
    }
    val sorted = list.sorted()
    return sorted.last() * sorted[sorted.count() - 2]
}

private fun executeRounds(
    monkeys: List<Monkey>,
    rounds: Long,
    divideWorryLevel: Boolean = true,
    maxLongDivisor: Long? = null
) {
    for (i in 1..rounds) {
        println("executing roung ${i}/${rounds}. Current value: ${getMutiplyOfMax2(monkeys)}")
        monkeys.forEach { monkey ->
            while (true) {
                val item = monkey.popFirstItem(divideWorryLevel, maxLongDivisor)
                if (item == null) {
                    break
                }
                val monkeyIndex = monkey.getMonkeyToThrowTo(item)
                monkeys[monkeyIndex].acceptItem(item)
            }
        }
    }
}

private fun formatInput(input: String): List<Monkey> {
    val blocks = input.split("\n\n")
    val monkeys = mutableListOf<Monkey>()

    blocks.forEach {
        val lines = it.split("\n")
        val index = getIndex(lines[0])
        var startingItems = getStartingItems(lines[1])
        val operation = getOperation(lines[2])
        val testOperation = getTestNumber(lines[3])
        val throwAction = getThrowAction(lines[4], lines[5])
        monkeys.add(Monkey(index, startingItems, operation, testOperation, throwAction))
    }

    return monkeys
}

private fun getIndex(input: String): Long {
    val (index) = "Monkey (.*):".toRegex().find(input)!!.destructured
    return index.trim().toLong()
}

private fun getStartingItems(input: String): List<Long> {
    val listOfItems = mutableListOf<Long>()

    val (itemList) = "Starting items:(.*)".toRegex().find(input)!!.destructured
    val items = itemList.split(",")
    items.forEach {
        listOfItems.add(it.trim().toLong())
    }
    return listOfItems
}

private fun getOperation(input: String): (Long) -> Long {
    val (operationAsString) = "Operation: (.*)".toRegex().find(input)!!.destructured
    val splitted = operationAsString.split(" ")
    return getOperation(splitted[2], splitted[3], splitted[4])
}

private fun getOperation(x: String, operator: String, y: String): (Long) -> Long {
    val xIsOld = x.equals("old")
    val yIsOld = y.equals("old")
    when {
        operator.equals("+") -> return { o -> (if (xIsOld) o else x.toLong()) + (if (yIsOld) o else y.toLong()) }
        operator.equals("-") -> return { o -> (if (xIsOld) o else x.toLong()) - (if (yIsOld) o else y.toLong()) }
        operator.equals("*") -> return { o -> (if (xIsOld) o else x.toLong()) * (if (yIsOld) o else y.toLong()) }
        operator.equals("/") -> return { o -> (if (xIsOld) o else x.toLong()) / (if (yIsOld) o else y.toLong()) }
        else -> throw Exception()
    }
}

private fun getTestNumber(input: String): Long {
    val (numberAsString) = "Test: divisible by (.*)".toRegex().find(input)!!.destructured
    val number = numberAsString.toLong()
    return number
}

private fun getThrowAction(input1: String, input2: String): (Boolean) -> Int {
    val (numberAsString1) = "If true: throw to monkey (.*)".toRegex().find(input1)!!.destructured
    val (numberAsString2) = "If false: throw to monkey (.*)".toRegex().find(input2)!!.destructured

    return { b -> if (b) numberAsString1.toInt() else numberAsString2.toInt() }
}

private data class Monkey(
    val index: Long,
    val startingItems: List<Long>,
    val operation: (Long) -> Long,
    val testDivisor: Long,
    val throwAction: (Boolean) -> Int,

    ) {
    var inspectedItems = 0L
        private set

    lateinit var items: MutableList<Long>

    init {
        items = mutableListOf<Long>()
        items.addAll(startingItems)
    }

    fun acceptItem(item: Long) {
        items.add(item)
    }

    fun getMonkeyToThrowTo(worryLevel: Long): Int {
        val divisible = worryLevel % testDivisor == 0L
        val idx = throwAction(divisible)

        return idx
    }

    fun popFirstItem(divideWorryLevel: Boolean = true, maxLongDivisor: Long? = null): Long? {
        if (items.count() == 0) {
            return null
        }
        val item = items.get(0)
        items.removeFirst()
        val newVal = inspectItem(item, divideWorryLevel, maxLongDivisor)

        return newVal
    }

    private fun inspectItem(item: Long, divideWorryLevel: Boolean = true, maxLongDivisor: Long? = null): Long {
        inspectedItems++
        val newVal = operation(item)
        val dividedVal = if (divideWorryLevel) newVal / 3 else newVal
        var cutOffVal = dividedVal
        if (maxLongDivisor != null) {
            cutOffVal = cutOffVal % maxLongDivisor
        }

        return cutOffVal
    }
}
