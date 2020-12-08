import java.io.File

typealias Bag = String
typealias BagContents = Map<Bag, Int>
typealias BagInfo = Map<Bag, BagContents>
typealias ParentInfo = Map<Bag, Set<Bag>>

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

fun BagInfo.parentInfo(): ParentInfo {
    val parentInfo: MutableMap<Bag, MutableSet<Bag>> = mutableMapOf()
    this.forEach { (parentBag, contents) ->
        contents.forEach { (childBag, _) ->
            val parentBags: MutableSet<Bag>? = parentInfo[childBag]
            when {
                parentBags != null -> parentBags.add(parentBag)
                else -> parentInfo[childBag] = mutableSetOf(parentBag)
            }
        }
    }
    return parentInfo
}

fun Bag.allParents(parentInfo: ParentInfo): Set<Bag> {
    val directParents: Set<Bag> = parentInfo[this] ?: return setOf()
    return directParents.fold(directParents.toMutableSet()) { allParents, directParent ->
        allParents.addAll(directParent.allParents(parentInfo))
        allParents
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
    val parentInfo: ParentInfo = bagInfo.parentInfo()
    val myShinyGoldBag: Bag = "shiny gold"

    val answer1 = myShinyGoldBag.allParents(parentInfo).size
    val answer2 = myShinyGoldBag.nestedBagCount(bagInfo)

    println("--- Day 7: Handy Haversacks ---")
    println(answer1)
    println("--- Part Two ---")
    println(answer2)
}
