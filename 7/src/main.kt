import java.io.File

typealias Bag = String
typealias BagInfo = Map<Bag, BagContents>
typealias BagContents = Map<Bag, Int>

fun String.cleanedOneBagContentString(): String {
    return this.trim().removeSuffix(".").removeSuffix("s").removeSuffix(" bag").trim()
}

fun String.parseOneBagContent(): Pair<Bag, Int> {
    val cleanedString = this.cleanedOneBagContentString()
    val firstSpaceIndex = cleanedString.indexOf(' ')
    val count: Int = cleanedString.substring(0, firstSpaceIndex).toInt()
    val bag: Bag = cleanedString.substring(firstSpaceIndex + 1).trim()
    return bag to count
}

fun String.parseBagContents(): BagContents = when {
    this.split(' ')[0] == "no" -> mapOf()
    else -> this
        .split(", ")
        .map { it.parseOneBagContent() }
        .toMap()
}

fun String.parseOneBagInfo(): Pair<Bag, BagContents> {
    val splits = this.split(" bags contain ")
    val bag: Bag = splits[0]
    val contents: BagContents = splits[1].parseBagContents()
    return bag to contents
}

fun String.parseBagInfo(): BagInfo = this
    .split('\n')
    .map { it.parseOneBagInfo() }
    .toMap()

fun Bag.doesHold(otherBag: Bag, info: BagInfo) = this.doesHold(otherBag, info, mutableSetOf())

fun Bag.doesHold(otherBag: Bag, info: BagInfo, visitedBags: MutableSet<Bag>): Boolean {
    if (visitedBags.contains(this)) return false
    visitedBags.add(this)

    val contents: BagContents? = info[this]
    return when {
        contents == null -> false
        contents.containsKey(otherBag) -> true
        else -> contents.any { (childBag, _) -> childBag.doesHold(otherBag, info, visitedBags) }
    }
}

fun Bag.nestedBagCount(info: BagInfo): Long {
    val contents: BagContents = info[this] ?: return 0L
    return contents
        .map { (childBag, count) -> count + count * childBag.nestedBagCount(info) }
        .fold(0L) { acc, count -> acc + count }
}

fun main() {
    val data: String = File("input.txt").readText()
    val bagInfo: BagInfo = data.parseBagInfo()
    val myShinyGoldBag: Bag = "shiny gold"

    val answer1 = bagInfo.count { (bag, _) -> bag.doesHold(myShinyGoldBag, bagInfo) }
    val answer2 = myShinyGoldBag.nestedBagCount(bagInfo)

    println("--- Day 7: Handy Haversacks ---")
    println(answer1)
    println("--- Part Two ---")
    println(answer2)
}
