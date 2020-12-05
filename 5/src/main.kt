import java.io.File
import kotlin.math.pow

enum class HalvingStrategy { UPPER, LOWER }

typealias Seats = Set<Int>

val IntRange.size: Int
    get() = this.last - this.first

val IntRange.halfSize: Int
    get() = this.size / 2

val IntRange.upperHalf: IntRange
    get() = (this.first + this.halfSize + 1)..this.last

val IntRange.lowerHalf: IntRange
    get() = this.first..(this.first + this.halfSize)

fun String.halveUntilOneValue(halvingStrategy: (Char) -> HalvingStrategy): Int {
    var range = 0 until 2f.pow(this.length).toInt()
    this.forEach { char ->
        val strategy: HalvingStrategy = halvingStrategy.invoke(char)
        range = when (strategy) {
            HalvingStrategy.UPPER -> range.upperHalf
            HalvingStrategy.LOWER -> range.lowerHalf
        }
    }
    return range.first
}

fun String.parseSeatId(): Int {
    val rowString = this.substring(0, 7)
    val columnString = this.substring(7)

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
    println(seatIds.max())

    println("--- Part Two ---")
    println(seatIds.findMySeatId())
}