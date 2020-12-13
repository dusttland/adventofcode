import java.io.File
import java.util.*

fun first(data: String) {
    val lines = data.split('\n')
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

    println((time - myTime) * busId)
}

fun List<Long?>.doesFit(t: Long, atIndex: Int): Boolean {
    this.forEachIndexed { index, busId ->
        if (busId != null && ((t - atIndex + index) % busId) != 0L) {
            return@doesFit false
        }
    }
    return true
}

fun Long.pairWith(other: Long, indexDif: Int): Long {
    var timestamp = this
    while (true) {
        if ((timestamp + indexDif) % other == 0L) {
            break
        } else {
            timestamp += this
        }
    }
    return timestamp
}

data class Strat(val start: Long = 0, val step: Long = 1) {
    fun pairWith(other: Long, indexDif: Int): Long {
        var timestamp = this.start
        while (true) {
            if ((timestamp + indexDif) % other == 0L) {
                break
            } else {
                timestamp += this.step
            }
        }
        return timestamp
    }
}

fun second(data: String) {
    val lines = data.split('\n')
    val buses: List<Long?> = lines[1].split(',').map { it.toLongOrNull() }



//    val firstNotNull = buses.filterNotNull().first()
//    val max = buses.filterNotNull().maxOrNull()!!
//    val maxNumberIndex = buses.indexOf(max)

    val sortedNotNullBuses = buses.indices.zip(buses).filter { it.second != null }
    val queue = LinkedList(sortedNotNullBuses)
//    var strategy = Strat(max, max)
    var strategy = Strat()

    while (queue.isNotEmpty()) {
        val pair = queue.poll()
        val number = pair.second ?: break
//        if (number == max) continue
        val indexDif = pair.first

        val success = strategy.pairWith(number, indexDif)
        strategy = Strat(success, strategy.step * number)

//        println("---")
//        println("Success: $success")
//        println("max: $max, min: $number, index: $indexDif")
//        println(strategy)
//        for (i in 0..max*number*3) {
//            val remainder = ((success + i) + (indexDif)) % number
//            val remainder2 = ((success + i)) % max
//            if (remainder == 0L && remainder2 == 0L)
//                println("i = $i")
//        }
    }

//    println(strategy)
    println(strategy.start)


//    val pair = queue.poll()
//    val success = max.pairWith(pair.second!!, pair.first - atIndex)
//    println("Pair success: $success")
//
//
//
//    val min = pair.second!!
//    val success2 = success * (1 + min)
//    val strat = Strat(success, max*min)
//    println(strat)
//    println("max: $max, min: $min, index: ${pair.first - atIndex}")
//    println(((success) + (atIndex - pair.first)) % min)
//
//    for (i in 0..max*min*3) {
//        val remainder = ((success + i) + (pair.first - atIndex)) % min
//        val remainder2 = ((success + i)) % max
//        if (remainder == 0L && remainder2 == 0L)
//            println("i = $i")
//    }
//
//    println("----")
//
//    val pair3 = queue.poll()
//    val min3 = pair3.second!!
//    val success3 = strat.pairWith(min3, pair3.first - atIndex)
//    val strat3 = Strat(success3, max * min3)
//    println("Success: $success3")
//    println("min: $min3, index: ${pair3.first - atIndex}")
//    println("Strat: $strat3")
//    for (i in 0..max*min3*3) {
//        val remainder = ((success3 + i) + (pair3.first - atIndex)) % min3
//        val remainder2 = ((success3 + i)) % max
//        if (remainder == 0L && remainder2 == 0L)
//            println("i = $i")
//    }
//
//    println("----")
//
//    val pair4 = queue.poll()
//    val min4 = pair4.second!!
//    val success4 = strat3.pairWith(min4, pair4.first - atIndex)
//    val strat4 = Strat(success4, max * min4)
//    println("Success: $success4")
//    println("min: $min4, index: ${pair4.first - atIndex}")
//    println("Strat: $strat4")
//    for (i in 0..max*min4*3) {
//        val remainder = ((success4 + i) + (pair4.first - atIndex)) % min4
//        val remainder2 = ((success4 + i)) % max
//        if (remainder == 0L && remainder2 == 0L)
//            println("i = $i")
//    }


//    var count = 0
//    var timestamp: Long = strategy.start
//    while (true) {
//        count++
//        when {
//            buses.doesFit(timestamp, maxNumberIndex) -> break
//            else -> timestamp += strategy.step
//        }
//    }
//    println(count)
//    println(timestamp - maxNumberIndex)
}

fun main() {
    val data: String = File("input.txt").readText()
    first(data)
    second(data)
}
