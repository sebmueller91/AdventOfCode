import java.io.BufferedReader
import java.io.File
import java.lang.Exception
import java.lang.StringBuilder

fun main(args: Array<String>) {
    val bufferedReader: BufferedReader = File("../input.txt").bufferedReader()
    val inputString = bufferedReader.use { it.readText() }

    val packetPairs = formatInput(inputString)
    println("Problem 1: " + countPacketPairsInOrder(packetPairs))

    val inputString2 = inputString + "" + "\n[[2]]" + "\n[[6]]"
    val packets = formatInput2(inputString2)
    val sortedPackets = sortPackets(packets)

    val dividerPacket1 = PacketEntry.ListEntry(listOf(PacketEntry.ListEntry(listOf(PacketEntry.IntegerEntry(2)))))
    val dividerPacket2 = PacketEntry.ListEntry(listOf(PacketEntry.ListEntry(listOf(PacketEntry.IntegerEntry(6)))))

    val pos1 = getPositionOfPacket(sortedPackets, dividerPacket1)
    val pos2 = getPositionOfPacket(sortedPackets, dividerPacket2)
    println("Problem 2: " + pos1 * pos2)
    // printPackets(sortedPackets)
}

private fun printPackets(packets: List<PacketEntry>) {
    packets.forEach {
        println(it.asString())
    }
}

private fun getPositionOfPacket(packets: List<PacketEntry>, packet: PacketEntry): Int {
    for (i in 0..packets.size - 1) {
        if (packets[i].equals(packet)) {
            return i+1
        }
    }
    throw Exception()
}

private fun sortPackets(packets: List<PacketEntry>): List<PacketEntry> {
    var sortedPackets = mutableListOf<PacketEntry>()
    packets.forEach {
        insertPacket(sortedPackets, it)
    }
    return sortedPackets
}

private fun insertPacket(sortedPackets: MutableList<PacketEntry>, packet: PacketEntry) {
    var idx = 0
    for (i in 0..sortedPackets.size) {
        idx = i
        if (i == sortedPackets.size || PacketPair(packet, sortedPackets[i]).listsAreInOrdner()) {
            break
        }
    }
    sortedPackets.add(idx, packet)
}

private fun countPacketPairsInOrder(pairs: List<PacketPair>): Int {
    var sum = 0
    for (i in 0..pairs.size - 1) {
//        println("Checking packacke ${i + 1}")
        if (pairs[i].listsAreInOrdner()) {
            sum += i + 1
        }
    }
    return sum
}

private fun formatInput(input: String): List<PacketPair> {
    val packagePairs = mutableListOf<PacketPair>()
    val packagePairsInput = input.split("\n\n")

    packagePairsInput.forEach {
        val lines = it.split("\n")
        packagePairs.add(PacketPair(resolveList(lines[0]), resolveList(lines[1])))
    }

    return packagePairs
}

private fun formatInput2(input: String): List<PacketEntry> {
    val packets = mutableListOf<PacketEntry>()
    val splitted = input.split("\n")

    splitted.forEach {
        if (it.length != 0) {
            packets.add(resolveList(it))
        }
    }

    return packets
}

private fun resolveList(input: String): PacketEntry {
    if (input.length == 0) {
        return PacketEntry.ListEntry(listOf<PacketEntry>())
    } else if (!input.contains('[') && !input.contains('[') && !input.contains(',')) {
        return PacketEntry.IntegerEntry(input.toInt())
    }
    val startingBracketPos = 0
    if (input[startingBracketPos] != '[') {
        throw Exception("")
    }
    var closingBracketPos = findClosingBracketPos(input)
    if (input[closingBracketPos] != ']') {
        throw Exception("")
    }
    val cutOffInput = input.substring(startingBracketPos + 1, closingBracketPos)

    var list = mutableListOf<PacketEntry>()
    val splittedInput = splitByCommaOnLowestLevel(cutOffInput)

    splittedInput.forEach {
        list.add(resolveList(it))
    }

    return PacketEntry.ListEntry(list)
}

private fun splitByCommaOnLowestLevel(input: String): List<String> {
    val list = mutableListOf<String>()
    var curPos = 0
    var level = 0

    for (i in 0..input.count() - 1) {
        if (input[i] == '[') {
            level++
        } else if (input[i] == ']') {
            level--
        } else if (level == 0 && input[i] == ',') {
            list.add(input.substring(curPos, i))
            curPos = i + 1
        }
    }

    list.add(input.substring(curPos, input.count()))

    return list
}

private fun findClosingBracketPos(input: String): Int {
    var openInnerBrackets = 0
    for (i in 1..input.length - 1) {
        if (input[i] == ']') {
            if (openInnerBrackets == 0) {
                return i
            } else {
                openInnerBrackets--
            }
        } else if (input[i] == '[') {
            openInnerBrackets++
        }
    }
    throw Exception("No closing bracket found in string <$input>")
}

private data class PacketPair(
    val packet1: PacketEntry,
    val packet2: PacketEntry
) {
    fun listsAreInOrdner(): Boolean {
        return elementsInOrder(packet1, packet2) != Order.FALSE
    }

    private fun elementsInOrder(l: PacketEntry, r: PacketEntry): Order {
        if (l is PacketEntry.IntegerEntry && r is PacketEntry.IntegerEntry) {
            return l.compareTo(r)
        } else {
            val ll = when (l) {
                is PacketEntry.ListEntry -> l
                else -> PacketEntry.ListEntry(listOf(l))
            }
            val rr = when (r) {
                is PacketEntry.ListEntry -> r
                else -> PacketEntry.ListEntry(listOf(r))
            }

            for (i in 0..ll.values.size - 1) {
                if (rr.values.size <= i) {
//                    println("Right side ran out of items, so inputs are not in the right order")
                    return Order.FALSE
                }
                var inOrder = elementsInOrder(ll.values[i], rr.values[i])
                if (inOrder != Order.SAME) {
                    return inOrder
                }
            }
            if (rr.values.size == ll.values.size) {
                return Order.SAME
            }
//            println("Left side ran out of items, so inputs are in the right order")
            return Order.TRUE
        }
    }
}

private enum class Order {
    TRUE, FALSE, SAME
}

private sealed class PacketEntry {
    abstract fun asString(): String

    data class ListEntry(val values: List<PacketEntry>) : PacketEntry() {
        fun compareTo(o: ListEntry): Order {
            throw Exception("not implemented")
        }

        override fun asString(): String {
            val strBld = StringBuilder("[")
            for (i in 0..values.size - 1) {
                strBld.append(values[i].asString())
                if (i != values.size - 1) {
                    strBld.append(",")
                }
            }
            strBld.append("]")
            return strBld.toString()
        }
    }

    data class IntegerEntry(val value: Int) : PacketEntry() {
        fun compareTo(o: IntegerEntry): Order {
//            println("Compare ${this.value} vs ${o.value}")
            when {
                value < o.value -> {
//                    println("Left side is smaller, so inputs are in the right order")
                    return Order.TRUE
                }

                value > o.value -> {
//                    println("Right side is smaller, so inputs are not in the right order")
                    return Order.FALSE
                }

                else -> return Order.SAME
            }
        }

        override fun asString(): String {
            return value.toString()
        }
    }
}