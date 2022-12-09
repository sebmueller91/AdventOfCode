import java.io.BufferedReader
import java.io.File

private const val MAX_FILESIZE = 100000
private const val MAX_HD = 70000000
private const val UPDATE_SIZE = 30000000

fun main(args: Array<String>) {
    val bufferedReader: BufferedReader = File("../input.txt").bufferedReader()
    val inputString = bufferedReader.use { it.readText() }

    val rootDir = inputStringToFileSystem(inputString)

    val sizeNeeded = UPDATE_SIZE - (MAX_HD - rootDir.getSize())
    //printFileStructure(rootDir)

    println(calculateSizeOfFoldersInSubFileSystem(rootDir))

    println(getSpaceOfDirectoryClosestToThreshold(rootDir, sizeNeeded))
}

private fun printFileStructure(directory: Directory, depth: Int = 0) {
    printTabs(depth)
    println("- ${directory.name} (dir, size=${directory.getSize()})")
    directory.children.forEach {
        printFileStructure(it, depth + 1)
    }
    directory.files.forEach {
        printTabs(depth + 1)
        println("- ${it.name} (file, size =${it.size})")
    }
}

private fun printTabs(number: Int) {
    for (i in 1..number) {
        print("\t")
    }
}

private fun getSpaceOfDirectoryClosestToThreshold(directory: Directory, size_needed: Int): Int {
    var bestValue = if (directory.getSize() >= size_needed) directory.getSize() else Int.MAX_VALUE

    directory.children.forEach {
        val bestOfChild = getSpaceOfDirectoryClosestToThreshold(it, size_needed)

        if (bestOfChild in size_needed until bestValue) {
            bestValue = bestOfChild
        }
    }
    return bestValue
}

private fun inputStringToFileSystem(input: String): Directory {
    val lines = input.split("\n")
    val rootDir = Directory(null, "/")
    var curDir = rootDir

    lines.forEach({
        if (it.startsWith("$")) {
            var arg = it.split(" ")[1]
            if (arg.equals("ls")) {
                // Do nothing
            } else { // cd
                val dirName = it.split(" ").last()

                if (dirName.equals("..")) {
                    curDir = curDir.parentDir!!
                } else if (dirName.equals("/")) {
                    // Do nothing
                } else {
                    curDir = curDir.children.filter { it.name.equals(dirName) }.first()
                }
            }
        } else {
            if (it.startsWith("dir")) {
                curDir.children.add(Directory(curDir, it.split(" ").last()))
            } else {
                val splittedArg = it.split(" ")
                var curFileSize = splittedArg[0].toInt()
                curDir.files.add(LocalFile(curFileSize, splittedArg[1]))
            }
        }
    })

    return rootDir
}

private fun calculateSizeOfFoldersInSubFileSystem(dir: Directory): Int {
    var size = 0
    size += if (dir.getSize() <= MAX_FILESIZE) dir.getSize() else 0
    dir.children.forEach({
        size += calculateSizeOfFoldersInSubFileSystem(it)
    })
    return size
}

class Directory(val parentDir: Directory?, val name: String) {
    val children: MutableList<Directory> = mutableListOf<Directory>()
    val files: MutableList<LocalFile> = mutableListOf<LocalFile>()
    private var size: Int? = null
        private set

    fun getSize(): Int {
        return size ?: calculateSize()
    }

    private fun calculateSize(): Int {
        var curSize = 0
        files.forEach({
            curSize += it.size
        })
        children.forEach({
            curSize += it.getSize()
        })

        size = curSize
        return curSize
    }
}

class LocalFile(val size: Int, val name: String) {

}
