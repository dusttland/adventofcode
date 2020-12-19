import java.io.File
import java.security.InvalidParameterException

typealias Links = List<List<Int>>

abstract class Rule
data class RuleLink(val links: Links) : Rule()
data class RuleValue(val value: String) : Rule()

data class PuzzleInput(val rules: Map<Int, Rule>, val strings: Set<String>) {
    private val cache: MutableMap<Int, Set<String>> = mutableMapOf()

    fun valuesOfRule(index: Int): Set<String> {
        val values: Set<String>? = this.cache[index]
        return when {
            values != null -> values
            else -> {
                val dugValues: Set<String> = this.digValuesOfRule(index)
                this.cache[index] = dugValues
                dugValues
            }
        }
    }

    private fun combination(indexList: List<Int>): Set<String> {
        if (indexList.isEmpty()) return setOf("")
        val values = this.valuesOfRule(indexList[0])
        val childCombinations = this.combination(indexList.subList(1, indexList.size))
        val strings = mutableSetOf<String>()
        for (string in values) {
            for (child in childCombinations) {
                strings.add(string + child)
            }
        }
        return strings
    }

    private fun digValuesOfRule(index: Int): Set<String> = when (val rule = this.rules[index]) {
        is RuleLink -> rule.links
            .filter { !it.contains(index) }
            .fold(mutableSetOf()) { acc, lst -> acc.addAll(this.combination(lst)); acc }
        is RuleValue -> setOf(rule.value)
        else -> throw InvalidParameterException("Rule with index $index does not exist.")
    }
}

fun String.substrings(size: Int): List<String> {
    val substrings = mutableListOf<String>()
    val count = this.length / size
    var cursor = 0
    repeat(count) {
        substrings.add(this.substring(cursor, cursor + size))
        cursor += size
    }
    return substrings
}

fun PuzzleInput.isValid2(string: String): Boolean {
    val values42 = this.valuesOfRule(42)
    val values31 = this.valuesOfRule(31)

    val len = values42.first().length
    val substrings = string.substrings(len)
    val count42 = substrings.count { it in values42 }
    val count31 = substrings.count { it in values31 }

    if (substrings.size < 3) return false
    if (count42 + count31 != substrings.size) return false
    if (count31 >= count42) return false

    var isTimeFor31 = false
    substrings.forEach { substring ->
        if (!isTimeFor31) {
            if (substring !in values42) {
                isTimeFor31 = true
                if (substring !in values31) return false
            }
        } else {
            if (substring !in values31) return false
        }
    }

    return substrings.first() in values42 && substrings.last() in values31
}

fun String.parseRule(): Rule {
    return when {
        this.contains(Regex("\"[a-z]+\"")) -> RuleValue(this.trim('\"'))
        else -> {
            val links: Links = this
                .split(" | ")
                .map { orSplit ->
                    orSplit.split(' ').map { andSplit -> andSplit.toInt() }
                }
            RuleLink(links)
        }
    }
}

fun String.parseIndexRulePair(): Pair<Int, Rule> {
    val splits = this.split(": ")
    val index = splits[0].toInt()
    val rule: Rule = splits[1].parseRule()
    return index to rule
}

fun String.parseRules(): Map<Int, Rule> = this
    .split('\n')
    .asSequence()
    .map { it.trim() }
    .filter { it.isNotBlank() }
    .map { it.parseIndexRulePair() }
    .toMap()

fun String.parseStrings(): Set<String> = this.split('\n').map { it.trim() }.toSet()

fun String.parsePuzzleInput(): PuzzleInput {
    val splits: List<String> = this.split("\n\r\n")
    val rules: Map<Int, Rule> = splits[0].parseRules()
    val strings: Set<String> = splits[1].parseStrings()
    return PuzzleInput(rules, strings)
}

fun main() {
    val data: String = File("input.txt").readText()
    val input = data.parsePuzzleInput()
    println("Part 1: ${input.valuesOfRule(0).count { it in input.strings }}")
    println("Part 2: ${input.strings.count { input.isValid2(it) }}")
}
