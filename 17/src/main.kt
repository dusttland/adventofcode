import java.io.File

typealias Coords = List<Int>
typealias ActiveCubes = Set<Coords>

fun Coords.withMutableNeighbours(): MutableSet<Coords> {
    if (this.isEmpty()) return mutableSetOf(listOf())
    val childDimensionNeighbours = this.subList(1, this.size).withMutableNeighbours()
    val neighbours = mutableSetOf<Coords>()
    for (i in -1..1) {
        childDimensionNeighbours.forEach {
            val newCoords = mutableListOf(this[0] + i)
            newCoords.addAll(it)
            neighbours.add(newCoords)
        }
    }
    return neighbours
}

fun Coords.neighbours(): Set<Coords> {
    val neighbours: MutableSet<Coords> = this.withMutableNeighbours()
    neighbours.remove(this)
    return neighbours
}

fun Coords.withNeighbours(): Set<Coords> = this.withMutableNeighbours()

fun ActiveCubes.isActive(coords: Coords): Boolean = this.contains(coords)

fun ActiveCubes.coordsToCheck(): Set<Coords> {
    return this.fold(mutableSetOf()) { acc, coords -> acc.addAll(coords.withNeighbours()); acc }
}

fun ActiveCubes.cycled(): ActiveCubes {
    return this.coordsToCheck().fold(mutableSetOf()) { acc, coords ->
        val activeCount = coords.neighbours().count { this.isActive(it) }
        val isActive = this.isActive(coords)
        when {
            isActive && (activeCount == 2 || activeCount == 3) -> acc.add(coords)
            !isActive && activeCount == 3 -> acc.add(coords)
        }
        acc
    }
}

fun String.parseActiveCubes(dimensions: Int): ActiveCubes {
    val activeCubes = mutableSetOf<Coords>()
    val lines = this.split('\n')
    lines.forEachIndexed { lineIndex, line ->
        line.forEachIndexed { index, char ->
            if (char == '#') {
                val coords = mutableListOf<Int>()
                repeat(dimensions - 2) { coords.add(0) }
                coords.add(lineIndex)
                coords.add(index)
                activeCubes.add(coords)
            }
        }
    }
    return activeCubes
}

fun main() {
    val data: String = File("input.txt").readText()

    var cubes: ActiveCubes = data.parseActiveCubes(dimensions = 3)
    repeat(6) { cubes = cubes.cycled() }
    println("Part 1: ${cubes.size}")

    cubes = data.parseActiveCubes(dimensions = 4)
    repeat(6) { cubes = cubes.cycled() }
    println("Part 2: ${cubes.size}")
}
