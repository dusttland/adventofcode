import java.io.File

const val EMPTY_SEAT = 'L'
const val OCCUPIED_SEAT = '#'

typealias Layout = Array<Array<Char>>

data class Position(val x: Int, val y: Int) {
    operator fun plus(other: Position) = Position(this.x + other.x, this.y + other.y)

    val neighbours: List<Position>
        get() = listOf(
            Position(this.x + 1, this.y + 1),
            Position(this.x + 1, this.y),
            Position(this.x + 1, this.y - 1),
            Position(this.x, this.y + 1),
            Position(this.x, this.y - 1),
            Position(this.x - 1, this.y + 1),
            Position(this.x - 1, this.y),
            Position(this.x - 1, this.y - 1)
        )
}

fun Layout.modified(newValueFunction: (pos: Position, prev: Char) -> Char): Layout {
    return Array(this.size) { y: Int ->
        Array(this[y].size) { x: Int ->
            val pos = Position(x, y)
            newValueFunction(pos, this.value(pos))
        }
    }
}

fun Layout.contains(pos: Position): Boolean = pos.y in this.indices && pos.x in this[pos.y].indices

fun Layout.value(pos: Position): Char = this[pos.y][pos.x]

fun Layout.valueOrNull(pos: Position): Char? = when {
    this.contains(pos) -> this.value(pos)
    else -> null
}

fun Layout.isOccupiedSeat(pos: Position): Boolean = this.valueOrNull(pos) == OCCUPIED_SEAT

fun Layout.isOccupiedSeatInDirection(pos: Position, direction: Position): Boolean {
    var currentPos = pos
    while (true) {
        currentPos += direction
        return when (this.valueOrNull(currentPos)) {
            null -> false
            EMPTY_SEAT -> false
            OCCUPIED_SEAT -> true
            else -> continue
        }
    }
}

fun Layout.countVisibleOccupiedSeats(pos: Position): Int {
    val directions = Position(0, 0).neighbours
    return directions.count { direction -> this.isOccupiedSeatInDirection(pos, direction) }
}

fun Layout.countAdjacentOccupiedSeats(pos: Position): Int = pos.neighbours.count { this.isOccupiedSeat(it) }

fun Layout.occupy1(): Layout = this.modified { pos: Position, prev: Char ->
    when {
        prev == EMPTY_SEAT && this.countAdjacentOccupiedSeats(pos) == 0 -> OCCUPIED_SEAT
        prev == OCCUPIED_SEAT && this.countAdjacentOccupiedSeats(pos) >= 4 -> EMPTY_SEAT
        else -> this.value(pos)
    }
}

fun Layout.occupy2(): Layout = this.modified { pos: Position, prev: Char ->
    when {
        prev == EMPTY_SEAT && this.countVisibleOccupiedSeats(pos) == 0 -> OCCUPIED_SEAT
        prev == OCCUPIED_SEAT && this.countVisibleOccupiedSeats(pos) >= 5 -> EMPTY_SEAT
        else -> prev
    }
}

fun Layout.countOccupiedSeats(): Int = this.fold(0) { acc, chars -> acc + chars.count { it == OCCUPIED_SEAT } }

fun Layout.runUntilStable(changeFunction: (Layout) -> Layout): Layout {
    var oldLayout: Layout = this
    var newLayout: Layout
    while (true) {
        newLayout = changeFunction(oldLayout)
        when {
            newLayout contentDeepEquals oldLayout -> return oldLayout
            else -> oldLayout = newLayout
        }
    }
}

fun String.parseLayout(): Layout = this
    .split('\n')
    .map { it.toCharArray().toTypedArray() }
    .toTypedArray()

fun main() {
    val data: String = File("input.txt").readText()
    val layout: Layout = data.parseLayout()

    val resultLayout1 = layout.runUntilStable { accLayout -> accLayout.occupy1() }
    println("Part 1: ${resultLayout1.countOccupiedSeats()}")

    val resultLayout2 = layout.runUntilStable { accLayout -> accLayout.occupy2() }
    println("Part 2: ${resultLayout2.countOccupiedSeats()}")
}
