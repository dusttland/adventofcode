import java.io.File

fun String.first(): Int {
    val lines = this.split('\n')
    val myTime = lines[0].toInt()
    val buses = lines[1].split(',').mapNotNull { it.toIntOrNull() }

    var time = myTime
    val busId: Int
    while (true) {
        when (val id = buses.find { time % it == 0 }) {
            null -> time++
            else -> { busId = id; break }
        }
    }

    return (time - myTime) * busId
}

fun String.second(): Long {
    val buses: List<Long?> = this.split('\n')[1].split(',').map { it.toLongOrNull() }

    var time: Long = 0L
    var step: Long = 1L
    buses.forEachIndexed { index: Int, busNumber: Long? ->
        if (busNumber == null) return@forEachIndexed
        while ((time + index) % busNumber != 0L) time += step
        step *= busNumber
    }

    return time
}

fun main() {
    val data: String = File("input.txt").readText()
    println("Part 1: ${data.first()}")
    println("Part 2: ${data.second()}")
}
