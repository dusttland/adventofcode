import java.io.File

infix fun List<Int>.numberAtTurn(targetTurn: Int): Int {
    if (targetTurn <= this.size) return this[targetTurn - 1]

    val occ: MutableMap<Int, Int> = LinkedHashMap(targetTurn / 6)
    var turn: Int = 0
    var lastNumber: Int = 0

    this.forEach {
        lastNumber = it
        occ[lastNumber] = turn + 1
        turn++
    }
    occ.remove(lastNumber)

    while (turn < targetTurn) {
        val occurrence: Int? = occ[lastNumber]
        occ[lastNumber] = turn
        lastNumber = when (occurrence) {
            null -> 0
            else -> turn - occurrence
        }
        turn++
    }

    return lastNumber
}

fun main() {
    val data: String = File("input.txt").readText()
    val numbers: List<Int> = data.split(',').map { it.trim().toInt() }

    println("Part 1: ${numbers numberAtTurn 2020}")
    println("Part 2: ${numbers numberAtTurn 30000000}")
}