import java.io.BufferedReader
import java.io.File

private const val max_filesize = 100000
private const val freeUpSpace_Threshold = 8381165

fun main(args: Array<String>) {
    val bufferedReader: BufferedReader = File("..\\testInput.txt").bufferedReader()
    val inputString = bufferedReader.use { it.readText() }

    val rootDir = inputStringToFileSystem(inputString)

    println(calculateSizeOfFoldersInSubFileSystem(rootDir))

    val rootDir2 = inputStringToFileSystem(inputString)
    println(getSpaceOfDirectoryClosestToThreshold(rootDir2))
}

private fun getSpaceOfDirectoryClosestToThreshold(directory: Directory): Int {
    var bestValue = if (directory.getSize() >= freeUpSpace_Threshold) directory.getSize() else Int.MAX_VALUE

    directory.children.forEach {
        val bestOfChild = getSpaceOfDirectoryClosestToThreshold(it)
        if (bestOfChild < bestValue && bestOfChild >= freeUpSpace_Threshold) {
            bestValue = bestOfChild
        }
    }
    return bestValue
}

private fun inputStringToFileSystem(input: String): Directory {
    val lines = input.split("\r\n")
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
                var curFileSize = it.split(" ")[0].toInt()
                curDir.files.add(LocalFile(curFileSize))
            }
        }
    })

    return rootDir
}

private fun calculateSizeOfFoldersInSubFileSystem(dir: Directory): Int {
    var size = 0
    size += if (dir.getSize() <= max_filesize) dir.getSize() else 0
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

class LocalFile(val size: Int) {

}
