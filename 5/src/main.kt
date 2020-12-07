import java.io.File

enum class HalvingStrategy { UPPER, LOWER }

typealias Seats = Set<Int>

fun bitAtPosition(position: Int): Int = 1 shl position

fun String.halveUntilOneValue(halvingStrategy: (Char) -> HalvingStrategy): Int {
    var result: Int = 0
    var bit: Int = bitAtPosition(this.length - 1)
    this.forEach { char ->
        val strategy = halvingStrategy.invoke(char)
        if (strategy == HalvingStrategy.UPPER) {
            result = result or bit
        }
        bit = bit shr 1
    }
    return result
}

fun String.parseSeatId(): Int {
    val rowString = this.substring(0, 7).trim()
    val columnString = this.substring(7).trim()

    val row: Int = rowString.halveUntilOneValue { char ->
        when (char) {
            'B' -> HalvingStrategy.UPPER
            else -> HalvingStrategy.LOWER
        }
    }
    val column: Int = columnString.halveUntilOneValue { char ->
        when (char) {
            'R' -> HalvingStrategy.UPPER
            else -> HalvingStrategy.LOWER
        }
    }

    return row * 8 + column
}

fun Seats.findMySeatId(): Int? {
    val precedingSeatId: Int? = this.find { id ->
        this.contains(id + 2) && !this.contains(id + 1)
    }
    return when {
        precedingSeatId != null -> precedingSeatId + 1
        else -> null
    }
}

fun String.parseSeatIds(): Seats = this
    .split('\n')
    .map { it.parseSeatId() }
    .toSet()

fun main() {
    val data: String = File("input.txt").readText()
    val seatIds: Seats = data.parseSeatIds()

    println("--- Day 5: Binary Boarding ---")
    println(seatIds.maxOrNull())

    println("--- Part Two ---")
    println(seatIds.findMySeatId())
}