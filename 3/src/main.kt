import java.io.File

typealias Trees = Set<Location>

data class Size(val width: Int, val height: Int)

data class Location(val x: Int, val y: Int) {
    operator fun plus(other: Location) = Location(this.x + other.x, this.y + other.y)
    operator fun rem(size: Size) = Location(this.x % size.width, this.y % size.height)
}

class Map(val size: Size, val trees: Trees) {
    fun containsAccordingToRules(location: Location): Boolean {
        return location.y < this.size.height
    }

    fun isTree(location: Location): Boolean {
        val adjustedLocation: Location = location % this.size
        return this.trees.contains(adjustedLocation)
    }

    fun countAccordingToRules(step: Location): Long {
        var location = Location(0, 0)
        var count = 0L
        while (true) {
            location += step
            if (!this.containsAccordingToRules(location)) break
            if (this.isTree(location)) count++
        }
        return count
    }
}

fun String.parseMap(): Map {
    val lines = this.split('\n')
    val size = Size(lines[0].length, lines.size)
    val trees: MutableSet<Location> = mutableSetOf()
    lines.forEachIndexed { y, line ->
        line.forEachIndexed { x, char ->
            if (char == '#') trees.add(Location(x, y))
        }
    }
    return Map(size, trees)
}

fun answer1(map: Map) {
    println("--- Day 3: Toboggan Trajectory ---")
    val step = Location(3, 1)
    val count = map.countAccordingToRules(step)
    println(count)
}

fun answer2(map: Map) {
    println("--- Part Two ---")
    val steps = listOf(
        Location(1, 1),
        Location(3, 1),
        Location(5, 1),
        Location(7, 1),
        Location(1, 2)
    )
    val answer: Long = steps
        .map { step -> map.countAccordingToRules(step) }
        .fold(1L) { acc, count -> acc * count }
    println(answer)
}

fun main() {
    val data: String = File("input.txt").readText()
    val map: Map = data.parseMap()
    answer1(map)
    answer2(map)
}
