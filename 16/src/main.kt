import java.io.File

typealias RangePair = Pair<IntRange, IntRange>
class Range(val identifier: String, val pair: RangePair)
typealias Ranges = List<Range>
typealias Ticket = List<Int>

class Data(val ranges: Ranges, val myTicket: Ticket, val nearbyTickets: List<Ticket>) {
    fun isValid(ticket: Ticket): Boolean = ticket.all { number -> number in this.ranges }
    fun validTickets(): List<Ticket> = this.nearbyTickets.filter { this.isValid(it) }
    fun invalidTickets(): List<Ticket> = this.nearbyTickets.filter { !this.isValid(it) }
}

operator fun RangePair.contains(number: Int) = number in this.first || number in this.second
operator fun Range.contains(number: Int) = number in this.pair
operator fun Ranges.contains(number: Int) = this.any { number in it }

fun Ranges.containingRangeIndexes(number: Int): Set<Int> = this
    .indices
    .filter { index -> number in this[index] }
    .toSet()

fun <T> List<T>.subList(fromIndex: Int): List<T> = this.subList(fromIndex, this.size)

fun List<Set<Int>>.allHaveSingleValue(): Boolean = this
    .toList()
    .all { it.size == 1 }

fun MutableList<Set<Int>>.trimFromNotFittingRanges(tickets: List<Ticket>, ranges: Ranges) {
    for (ticket in tickets) {
        ticket.forEachIndexed { index, number ->
            val validRangeIndexes = ranges.containingRangeIndexes(number)
            val currentIndexes = this[index]
            this[index] = currentIndexes.intersect(validRangeIndexes)
        }
        if (this.allHaveSingleValue()) break
    }
}

fun MutableList<Set<Int>>.trimFromValuesThatAreSingles() {
    val visitedValues: MutableSet<Int> = mutableSetOf()
    while (true) {
        val validSet: Set<Int> = this.find { it.size == 1 && !visitedValues.contains(it.first()) } ?: break
        visitedValues.add(validSet.first())

        this.forEachIndexed { index, otherSet ->
            if (otherSet.size == 1) return@forEachIndexed
            this[index] = otherSet - validSet
        }
    }
}

fun <T> List<Set<T>>.firstValues(): List<T> = this.map { it.first() }

fun List<Int>.rebaseToTicketIndexes() = this.indices.sortedBy { this[it] }

fun List<Int>.multiplied(): Long = this.fold(1L) { acc, value -> acc * value }

fun Data.secondAnswer(): Long? {
    val validTickets: List<Ticket> = this.validTickets()
    val setOfAllRangeIndexes: Set<Int> = this.ranges.indices.toSet()
    val fittingRangeIndexes: MutableList<Set<Int>> = this.ranges.indices.map { setOfAllRangeIndexes }.toMutableList()
    fittingRangeIndexes.trimFromNotFittingRanges(validTickets, this.ranges)
    fittingRangeIndexes.trimFromValuesThatAreSingles()
    if (!fittingRangeIndexes.allHaveSingleValue()) return null
    val rangeIndexes = fittingRangeIndexes.firstValues()
    val ticketNumberIndexes = rangeIndexes.rebaseToTicketIndexes()
    return ticketNumberIndexes
        .filterIndexed { rangeIndex, ticketNumberIndex -> this.ranges[rangeIndex].identifier.contains("departure") }
        .map { ticketNumberIndex -> this.myTicket[ticketNumberIndex] }
        .multiplied()
}

fun Data.firstAnswer(): Int {
    val invalidTickets = this.invalidTickets()
    val invalidNumbers = invalidTickets.fold(mutableListOf<Int>()) { acc, ticket ->
        acc.addAll(ticket.filter { number -> number !in this.ranges })
        acc
    }
    return invalidNumbers.sum()
}

fun String.parseTicket(): Ticket = this
    .split(',')
    .map { it.toInt() }

fun String.parseIntRange(): IntRange {
    val splits = this.split('-')
    return splits[0].toInt()..splits[1].toInt()
}

fun String.parseRangePair(): RangePair {
    val splits = this.split(" or ")
    return splits[0].parseIntRange() to splits[1].parseIntRange()
}

fun String.parseRange(): Range {
    val splits = this.split(": ")
    return Range(splits[0], splits[1].parseRangePair())
}

fun String.parseRanges(): Ranges = this
    .split('\n')
    .map { it.parseRange() }

fun String.parseMyTicket(): Ticket = this.split('\n')[1].parseTicket()

fun String.parseNearbyTickets(): List<Ticket> = this
    .split('\n')
    .subList(1)
    .map { it.parseTicket() }

fun String.parseData(): Data {
    val splits = this.split("\n\n")
    val ranges: Ranges = splits[0].parseRanges()
    val myTicket: Ticket = splits[1].parseMyTicket()
    val nearbyTickets: List<Ticket> = splits[2].parseNearbyTickets()
    return Data(ranges, myTicket, nearbyTickets)
}

fun main() {
    val fileContents: String = File("input.txt").readText()
    val data: Data = fileContents.parseData()
    println("Part 1: ${data.firstAnswer()}")
    println("Part 2: ${data.secondAnswer()}")
}