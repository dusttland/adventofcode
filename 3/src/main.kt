import java.io.File

typealias Trees = Set<Location>
typealias Step = Location

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

    fun countAccordingToRules(step: Step): Long {
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

fun printAnswer(map: Map, steps: List<Step>) {
    val answer: Long = steps
        .map { step -> map.countAccordingToRules(step) }
        .fold(1L) { acc, count -> acc * count }
    println(answer)
}

fun main() {
    val data: String = File("input.txt").readText()
    val map: Map = data.parseMap()

    println("--- Day 3: Toboggan Trajectory ---")
    printAnswer(map = map, steps = listOf(
        Step(3, 1))
    )

    println("--- Part Two ---")
    printAnswer(map = map, steps = listOf(
        Step(1, 1),
        Step(3, 1),
        Step(5, 1),
        Step(7, 1),
        Step(1, 2)
    ))
}
