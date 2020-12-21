import java.io.File

data class Food(val words: List<String>, val allergens: List<String>)

fun <T> List<T>.subList(fromIndex: Int) = this.subList(fromIndex, this.size)

fun String.parseAllergens(): List<String> = this
    .removeSuffix(")")
    .split(' ')
    .subList(1)
    .map { it.removeSuffix(",") }

fun String.parseFood(): Food {
    val splits = this.split(" (")
    val words = splits[0].split(' ')
    val allergens = splits[1].parseAllergens()
    return Food(words, allergens)
}

fun String.parseFoods(): List<Food> = this
    .split('\n')
    .map { it.trim().parseFood() }

fun List<Food>.allergenFoodsMap(): Map<String, List<Food>> = this
    .fold(mutableMapOf<String, MutableList<Food>>()) { acc, food ->
        val pairs = food.allergens.map { allergen -> allergen to food }
        pairs.forEach {
            val e = acc[it.first]
            if (e != null) e.add(it.second)
            else acc[it.first] = mutableListOf(it.second)
        }
        acc
    }

fun Map<String, List<Food>>.allergenPlausibleWordsMap(): Map<String, Set<String>> = this
    .toList()
    .map {
        val allergen = it.first
        val foods = it.second
        allergen to foods.fold(foods.first().words.toSet()) { acc, food -> acc.intersect(food.words) }
    }
    .toMap()

fun Map<String, Set<String>>.applyExclusion(): Map<String, String> {
    val newMap = mutableMapOf<String, MutableSet<String>>()
    this.forEach { (allergen, wordSet) -> newMap[allergen] = wordSet.toMutableSet() }
    val visited = mutableSetOf<String>()
    while (true) {
        val pair = newMap.toList().find { it.second.size == 1 && it.first !in visited }
        when {
            pair == null -> break
            else -> {
                val allergen = pair.first
                val word = pair.second.first()
                visited.add(allergen)
                newMap
                    .filter { (first, _) -> first != allergen }
                    .forEach { (_, wordSet) -> wordSet.remove(word) }
            }
        }
    }

    return newMap.map { (allergen, wordSet) -> allergen to wordSet.first() }.toMap()
}

fun List<Food>.allergenWordMap(): Map<String, String> = this
    .allergenFoodsMap()
    .allergenPlausibleWordsMap()
    .applyExclusion()

fun main() {
    val foods: List<Food> = File("input.txt").readText().parseFoods()
    val allergenWordMap: Map<String, String> = foods.allergenWordMap()

    val allergenWords: Set<String> = allergenWordMap.map { (_, word) -> word }.toSet()
    val allWords: List<String> = foods.fold(mutableListOf()) { acc, food -> acc.addAll(food.words); acc }
    val wordsWithoutAllergens = allWords.filter { it !in allergenWords }
    println("Part 1: ${wordsWithoutAllergens.size}")

    val sortedAllergenWords = allergenWordMap.toList().sortedBy { it.first }.map { it.second }
    val answer2 = sortedAllergenWords.fold("") { acc, s -> "$acc,$s" }.removePrefix(",")
    println("Part 2: $answer2")
}
