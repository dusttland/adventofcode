import Side.*
import java.io.File

typealias Edge = List<Boolean>
typealias Pattern = Set<Point>

data class Point(val x: Int, val y: Int) {
    operator fun plus(o: Point) = Point(this.x + o.x, this.y + o.y)
}

enum class Side {
    TOP, RIGHT, BOTTOM, LEFT;
    val opposite: Side
        get() = when (this) {
            TOP -> BOTTOM
            RIGHT -> LEFT
            BOTTOM -> TOP
            LEFT -> RIGHT
        }
}

open class Image(strings: List<String>) {
    var strings: List<String> = strings
        private set

    fun edge(side: Side): Edge = when (side) {
        TOP -> this.strings.first().map { it == '#' }
        RIGHT -> this.strings.map { it.last() == '#' }
        BOTTOM -> this.strings.last().map { it == '#' }.reversed()
        LEFT -> this.strings.map { it.first() == '#' }.reversed()
    }

    fun rotateLeft() {
        val size = this.strings.size
        val newStrings: List<List<Char>> = List(size) { y ->
            List(size) { x ->
                this.strings[size - 1 - x][y]
            }
        }
        this.strings = newStrings.map { it.asString() }
    }

    fun flipHorizontally() {
        this.strings = this.strings.map { it.reversed() }
    }

    fun rotateAndFlipUntil(func: () -> Boolean): Boolean {
        repeat(4) {
            if (func.invoke()) return true
            this.rotateLeft()
        }
        this.flipHorizontally()
        repeat(4) {
            if (func.invoke()) return true
            this.rotateLeft()
        }
        this.flipHorizontally()
        return false
    }
}

class Tile(
    val id: Int,
    strings: List<String>
) : Image(strings) {

    val neighbours: MutableMap<Side, Tile> = mutableMapOf()

    var isFixed: Boolean = false

    fun go(side: Side, tiles: Collection<Tile>): Tile {
        var currentTile = this
        while (true) {
            when (val nextTile = currentTile.find(side, tiles)) {
                null -> break
                else -> currentTile = nextTile
            }
        }
        return currentTile
    }

    fun find(side: Side, tiles: Collection<Tile>): Tile? {
        this.isFixed = true
        return when (val existing = this.neighbours[side]) {
            null -> {
                val tile = this.findBySearch(side, tiles)
                if (tile != null) {
                    this.neighbours[side] = tile
                    tile.neighbours[side.opposite] = this
                    tile.isFixed = true
                }
                tile
            }
            else -> existing
        }
    }

    private fun findBySearch(side: Side, tiles: Collection<Tile>): Tile? {
        fun doesLineUp(tile: Tile): Boolean = tile.edge(side.opposite).reversed() == this.edge(side)
        for (tile in tiles) {
            if (tile.id == this.id) continue
            if (tile.isFixed) {
                if (doesLineUp(tile)) return tile
            } else {
                val didFind = tile.rotateAndFlipUntil { doesLineUp(tile) }
                if (didFind) return tile
            }
        }
        return null
    }
}

fun List<String>.toHashTagPointSet(): Set<Point> = this.foldIndexed(mutableSetOf()) { y, acc, row ->
    acc.addAll(row.foldIndexed(mutableSetOf()) { x, acc2, char ->
        if (char == '#') acc2.add(Point(x, y))
        acc2
    })
    acc
}

fun List<Char>.asString(): String = this.fold("") { acc, char -> acc + char }

fun Collection<Tile>.constructImageMatrix(): List<List<Int>> {
    val matrix: MutableList<MutableList<Int>> = mutableListOf()
    var rowCursor: Tile? = this.first().go(TOP, this).go(LEFT, this)
    while (rowCursor != null) {
        var columnCursor: Tile? = rowCursor
        val row = mutableListOf<Int>()
        while (columnCursor != null) {
            row.add(columnCursor.id)
            columnCursor = columnCursor.find(RIGHT, this)
        }
        matrix.add(row)
        rowCursor = rowCursor.find(BOTTOM, this)
    }
    return matrix
}

infix fun List<Tile>.applyMatrix(matrix: List<List<Int>>): Image {
    val tileMap: Map<Int, Tile> = this.map { it.id to it }.toMap()
    val strings = mutableListOf<String>()
    matrix.forEach { row ->
        val rowTiles: List<Tile> = row.mapNotNull { tileMap[it] }
        val indices = 1 until rowTiles.first().strings.size - 1
        indices.forEach { index ->
            val string = rowTiles.fold("") { acc, tile ->
                val string = tile.strings[index]
                acc + tile.strings[index].substring(1, string.length - 1)
            }
            strings.add(string)
        }
    }
    return Image(strings)
}

infix fun Set<Point>.matchingPointsWith(pattern: Pattern): Set<Point> {
    val pWidth = pattern.maxOf { it.x }
    val pHeight = pattern.maxOf { it.y }
    val mWidth = this.maxOf { it.x }
    val mHeight = this.maxOf { it.y }

    if (mHeight < pHeight && mWidth < pWidth) return setOf()

    val matchingPoints: MutableSet<Point> = mutableSetOf()
    repeat(mHeight - pHeight) { yOffset ->
        repeat(mWidth - pWidth) { xOffset ->
            val offsetPoint = Point(xOffset, yOffset)
            val doesPatternMatch = pattern.all { this.contains(it + offsetPoint) }
            if (doesPatternMatch) matchingPoints.addAll(pattern.map { it + offsetPoint })
        }
    }
    return matchingPoints
}

fun <T> List<List<T>>.corners(): List<T> = listOf(
    this[0][0],
    this[0][this[0].size - 1],
    this[this.size - 1][0],
    this[this.size - 1][this[0].size - 1]
)

fun List<Int>.multiplied(): Long = this.fold(1L) { acc, int -> acc * int }

fun String.parseTile(): Tile {
    val splits = this.split('\n').map { it.trim() }.filter { it.isNotBlank() }
    val id = splits[0].removePrefix("Tile ").removeSuffix(":").toInt()
    val strings = splits.subList(1, splits.size)
    return Tile(id, strings)
}

fun String.parseTiles(): List<Tile> = this
    .split("\n\n")
    .map { it.trim().parseTile() }

fun String.parsePattern(): Pattern = this
    .split('\n')
    .toHashTagPointSet()

fun main() {
    val tiles: List<Tile> = File("input.txt").readText().parseTiles()
    val monsterPattern: Pattern = File("pattern.txt").readText().parsePattern()

    val idMatrix: List<List<Int>> = tiles.constructImageMatrix()
    val image: Image = tiles applyMatrix idMatrix

    var monsterPoints: Set<Point> = setOf()
    var hashTags: Set<Point> = setOf()
    image.rotateAndFlipUntil {
        hashTags = image.strings.toHashTagPointSet()
        monsterPoints = hashTags matchingPointsWith monsterPattern
        monsterPoints.isNotEmpty()
    }

    println("Part 1: ${idMatrix.corners().multiplied()}")
    println("Part 2: ${hashTags.size - monsterPoints.size}")
}
