import java.io.File

typealias Room = List<List<List<Boolean>>>

fun Room.value(c: Coords): Boolean = this[c.z][c.y][c.x]

val Room.coordsSize: Coords
    get() = Coords(this[0][0].size, this[0].size, this.size)

fun Room.forEachCoord(block: (Coords, Boolean) -> Unit) {
    for (z in this.indices) {
        for (y in this[z].indices) {
            for (x in this[z][y].indices) {
                val coords = Coords(x, y, z)
                block.invoke(coords, this.value(coords))
            }
        }
    }
}

fun Room.count(block: (Boolean) -> Boolean): Int {
    var count = 0
    for (z in this.indices) {
        for (y in this[z].indices) {
            for (x in this[z][y].indices) {
                val coords = Coords(x, y, z)
                if (block(this.value(coords))) count++
            }
        }
    }
    return count
}

fun Room.changed(block: (Room, Coords, Boolean) -> Boolean): Room {
    val size = this.coordsSize
    return List(size.z) { z ->
        List(size.y) { y ->
            List(size.x) { x ->
                val coords = Coords(x, y, z)
                block(this, coords, this.value(coords))
            }
        }
    }
}

fun Room.transform(coords: Coords): Room {
    val size = this.coordsSize
    return List(coords.z + size.z) { z ->
        List(coords.y + size.y) { y ->
            List(coords.x + size.x) { x ->
                val oldCoords = Coords(x, y, z) - coords
                this.valueOrFalse(oldCoords)
            }
        }
    }
}

fun Room.expand(coords: Coords): Room {
    val size = this.coordsSize
    return List(2 * coords.z + size.z) { z ->
        List(2 * coords.y + size.y) { y ->
            List(2 * coords.x + size.x) { x ->
                val oldCoords = Coords(x, y, z) - coords
                this.valueOrFalse(oldCoords)
            }
        }
    }
}

infix fun Room.contains(c: Coords): Boolean {
    return c.z in this.indices && c.y in this[c.z].indices && c.x in this[c.z][c.y].indices
}

fun Room.prettyPrint() {
    this.forEach { z ->
        z.forEach { y ->
            y.forEach { x -> val c = if (x) '#' else '.'; print(c) }
            println()
        }
        println()
    }
}

infix fun Coords.isIn(room: Room) = room contains this

fun Room.valueOrNull(coords: Coords): Boolean? = when {
    coords isIn this -> this.value(coords)
    else -> null
}

fun Room.valueOrFalse(coords: Coords): Boolean = this.valueOrNull(coords) ?: false

data class Coords(val x: Int, val y: Int, val z: Int) {
    override fun toString(): String = "(${this.x},${this.y},${this.z})"
    operator fun plus(other: Coords) = Coords(this.x + other.x, this.y + other.y, this.z + other.z)
    operator fun minus(other: Coords) = Coords(this.x - other.x, this.y - other.y, this.z - other.z)
    fun neighbours(): Set<Coords> {
        val set = mutableSetOf<Coords>()
        for (z in -1..1) {
            for (y in -1..1) {
                for (x in -1..1) {
                    set.add(Coords(this.x - x, this.y - y, this.z - z))
                }
            }
        }
        set.remove(this)
        return set
    }
}

fun String.parseRoom(): Room = listOf(this
    .split('\n')
    .map { charList -> charList.trim().map { char -> char == '#' } }
)

fun Room.cycled(): Room = this.expand(Coords(1, 1, 1)).changed { old, coords, value ->
    val nCount = coords.neighbours().count { old.valueOrFalse(it) }
    when {
        value && (nCount == 2 || nCount == 3) -> true
        !value && nCount == 3 -> true
        else -> false
    }
}

fun main() {
    val data: String = File("input.txt").readText()
    val room: Room = data.parseRoom()


    var cycledRoom: Room = room
    repeat(6) { cycledRoom = cycledRoom.cycled() }
    println("Part 1: ${cycledRoom.count { it }}")
}
