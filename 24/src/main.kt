import Direction.*
import java.io.File
import java.security.InvalidParameterException

enum class Direction {
    NE, E, SE, SW, W, NW
}

typealias Path = List<Direction>

data class Point(val x: Int, val y: Int)

infix fun Point.to(direction: Direction): Point = when {
    this.y % 2 == 0 -> when (direction) {
        NE -> Point(this.x, this.y - 1)
        E  -> Point(this.x + 1, this.y)
        SE -> Point(this.x, this.y + 1)
        SW -> Point(this.x - 1, this.y + 1)
        W  -> Point(this.x - 1, this.y)
        NW -> Point(this.x - 1, this.y - 1)
    }
    else -> when (direction) {
        NE -> Point(this.x + 1, this.y - 1)
        E  -> Point(this.x + 1, this.y)
        SE -> Point(this.x + 1, this.y + 1)
        SW -> Point(this.x, this.y + 1)
        W  -> Point(this.x - 1, this.y)
        NW -> Point(this.x, this.y - 1)
    }
}

fun Point.neighbours(): List<Point> = Direction.values().map { direction -> this to direction }

fun String.toDirection(): Direction = when (this) {
    "ne" -> NE
    "e"  -> E
    "se" -> SE
    "sw" -> SW
    "w"  -> W
    "nw" -> NW
    else -> throw InvalidParameterException("\"$this\" is not a direction.")
}

fun String.parsePath(): Path {
    val path = mutableListOf<Direction>()
    var cursor = 0
    while (cursor < this.length) {
        val strLength: Int = when (this[cursor]) {
            'n', 's' -> 2
            else -> 1
        }
        val str = this.substring(cursor, cursor + strLength)
        path.add(str.toDirection())
        cursor += strLength
    }
    return path
}

fun String.parsePaths(): List<Path> = this.split('\n').map { it.parsePath() }

fun List<Path>.blackTiles(): MutableSet<Point> = this.fold(mutableSetOf()) { acc, path ->
    val point = path.fold(Point(0, 0)) { point, direction -> point to direction }
    when {
        point in acc -> acc.remove(point)
        else -> acc.add(point)
    }
    acc
}

fun MutableSet<Point>.passDay() {
    val whiteTiles = this.fold(mutableSetOf<Point>()) { acc, blackTilePoint ->
        val whiteTilesAround = blackTilePoint.neighbours().filter { it !in this }
        acc.addAll(whiteTilesAround)
        acc
    }

    fun countBlackNeighbours(point: Point): Int = point.neighbours().filter { n -> n in this }.size

    val tilesToFlip = mutableListOf<Point>()
    this.forEach {
        val blackCount = countBlackNeighbours(it)
        if (blackCount == 0 || blackCount > 2) tilesToFlip.add(it)
    }

    whiteTiles.forEach {
        val blackCount = countBlackNeighbours(it)
        if (blackCount == 2) tilesToFlip.add(it)
    }

    tilesToFlip.forEach {
        when {
            it in this -> this.remove(it)
            else -> this.add(it)
        }
    }
}

fun MutableSet<Point>.passDays(count: Int) {
    repeat(count) { this.passDay() }
}

fun main() {
    val paths: List<Path> = File("input.txt").readText().parsePaths()

    val blackTiles: MutableSet<Point> = paths.blackTiles()
    println("Part 1: ${blackTiles.size}")

    blackTiles.passDays(100)
    println("Part 2: ${blackTiles.size}")
}
