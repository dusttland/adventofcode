import java.io.File

typealias Operations = MutableList<Operation>

data class Operation(val name: String, val value: Int)

class BootCode(private val operations: Operations) {
    var accValue: Int = 0
        private set

    private var cursor: Int = 0
    private val visitedCursors: MutableSet<Int> = mutableSetOf()

    fun run(): Boolean {
        this.reset()
        while (this.cursor < this.operations.size) {
            if (this.visitedCursors.contains(this.cursor))
                return false
            this.visitedCursors.add(this.cursor)

            val operation = this.operations[this.cursor]
            if (!this.runOperation(operation))
                return false
        }
        return true
    }

    private fun reset() {
        this.accValue = 0
        this.cursor = 0
        this.visitedCursors.clear()
    }

    private fun runOperation(operation: Operation): Boolean {
        when (operation.name) {
            "acc" -> this.acc(operation.value)
            "jmp" -> this.jmp(operation.value)
            "nop" -> this.nop()
            else -> return false
        }
        return true
    }

    private fun acc(value: Int) {
        this.accValue += value
        this.cursor++
    }

    private fun jmp(value: Int) {
        this.cursor += value
    }

    private fun nop() {
        this.cursor++
    }
}

fun Operation.swapped(): Operation {
    val newName = when (this.name) {
        "nop" -> "jmp"
        else -> "nop"
    }
    return Operation(newName, this.value)
}

fun String.parseOperation(): Operation {
    val splits = this.split(' ')
    val name: String = splits[0]
    val value: Int = splits[1].toInt()
    return Operation(name, value)
}

fun String.parseOperations(): Operations = this
    .split('\n')
    .map { it.parseOperation() }
    .toMutableList()

fun main() {
    val data: String = File("input.txt").readText()
    val operations: Operations = data.parseOperations()
    val bootCode = BootCode(operations)

    bootCode.run()
    println(bootCode.accValue)

    val swappableOperationIndexes = mutableListOf<Int>()
    operations.forEachIndexed { index, operation ->
        if (operation.name == "jmp" || operation.name == "nop")
            swappableOperationIndexes.add(index)
    }

    for (index in swappableOperationIndexes) {
        operations[index] = operations[index].swapped()
        if (bootCode.run())
            break
        operations[index] = operations[index].swapped()
    }

    println(bootCode.accValue)
}
