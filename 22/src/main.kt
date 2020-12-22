import java.io.File
import java.util.*

typealias Deck = LinkedList<Int>
typealias Decks = Pair<Deck, Deck>
typealias DeckHistory = MutableSet<List<Int>>

data class GameResult(val winner: Int, val deck: Deck)

val Deck.score: Int
    get() = this.mapIndexed { index, value -> value * (this.size - index) }.sum()

fun Deck.copied(): Deck = LinkedList(this)
fun Deck.copied(count: Int): Deck = LinkedList(this.subList(0, count))

fun Decks.copied(): Decks = this.first.copied() to this.second.copied()
fun Decks.copied(count1: Int, count2: Int) = this.first.copied(count1) to this.second.copied(count2)

fun <T> List<T>.subList(fromIndex: Int) = this.subList(fromIndex, this.size)

fun <T> List<T>.toLinkedList(): LinkedList<T> = LinkedList(this)

fun String.parseDeck(): Deck = this
    .split('\n')
    .subList(1)
    .map { it.trim().toInt() }
    .toLinkedList()

fun String.parseDecks(): Decks {
    val splits = this.split("\n\r\n")
    val p1 = splits[0].trim().parseDeck()
    val p2 = splits[1].trim().parseDeck()
    return p1 to p2
}

fun combat(decks: Decks): GameResult {
    while (decks.first.isNotEmpty() && decks.second.isNotEmpty()) {
        val c1 = decks.first.pop()
        val c2 = decks.second.pop()
        when {
            c1 > c2 -> {
                decks.first.add(c1)
                decks.first.add(c2)
            }
            else -> {
                decks.second.add(c2)
                decks.second.add(c1)
            }
        }
    }

    return when {
        decks.first.isEmpty() -> GameResult(2, decks.second)
        else -> GameResult(1, decks.first)
    }
}

fun recursiveCombat(decks: Decks): GameResult {
    val history: Pair<DeckHistory, DeckHistory> = Pair(mutableSetOf(), mutableSetOf())

    while (decks.first.isNotEmpty() && decks.second.isNotEmpty()) {
        val c1 = decks.first.pop()
        val c2 = decks.second.pop()

        val winningPlayer: Int = when {
            decks.first.size >= c1 && decks.second.size >= c2 -> { recursiveCombat(decks.copied(c1, c2)).winner }
            c1 > c2 -> 1
            else -> 2
        }

        when (winningPlayer) {
            1 -> {
                decks.first.add(c1)
                decks.first.add(c2)
            }
            else -> {
                decks.second.add(c2)
                decks.second.add(c1)
            }
        }

        if (decks.first in history.first || decks.second in history.second) {
            return GameResult(1, decks.first)
        } else {
            history.first.add(decks.first.copied())
            history.second.add(decks.second.copied())
        }
    }

    return when {
        decks.first.isEmpty() -> GameResult(2, decks.second)
        else -> GameResult(1, decks.first)
    }
}

fun main() {
    val decks: Decks = File("input.txt").readText().parseDecks()

    val combatResult: GameResult = combat(decks.copied())
    println("Part 1: ${combatResult.deck.score}")

    val recursiveCombatResult: GameResult = recursiveCombat(decks.copied())
    println("Part 2: ${recursiveCombatResult.deck.score}")
}
