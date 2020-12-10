import java.io.File
import java.util.PriorityQueue

typealias Heap = PriorityQueue<Int>

fun List<Int>.toMinHeap(): Heap = PriorityQueue(this)

fun List<Int>.toMaxHeap(): Heap = PriorityQueue<Int>(compareByDescending { it }).apply {
    this.addAll(this@toMaxHeap)
}

val List<Int>.answer1: Int
    get() {
        val minHeap: Heap = this.toMinHeap()
        var oneCount = 0
        var threeCount = 0
        var currentJoltage = 0
        while (minHeap.isNotEmpty()) {
            val nextJoltage = minHeap.poll()
            when (nextJoltage - currentJoltage) {
                3 -> threeCount++
                1 -> oneCount++
            }
            currentJoltage = nextJoltage
        }
        threeCount++
        return oneCount * threeCount
    }

val List<Int>.answer2: Long
    get() {
        val maxHeap: Heap = this.toMaxHeap()
        maxHeap.add(0)
        val map: MutableMap<Int, Long> = mutableMapOf(maxHeap.poll() to 1)
        while (maxHeap.isNotEmpty()) {
            val value: Int = maxHeap.poll()
            var count: Long = 0L
            for (i in 1..3) {
                count += map[value + i] ?: 0L
            }
            map[value] = count
        }
        return map[0]!!
    }

fun String.parseNumbers(): List<Int> = this
    .split('\n')
    .filter { it.isNotBlank() }
    .map { it.trim().toInt() }

fun main() {
    val data: String = File("input.txt").readText()
    val numbers = data.parseNumbers()
    println("Part 1: ${numbers.answer1}")
    println("Part 2: ${numbers.answer2}")
}
