import java.io.File

fun main() {
    val data: String = File("input.txt").readText()
    val groups: List<List<Set<Char>>> = data
        .split("\n\n")
        .map { persons: String ->
            persons
                .split('\n')
                .map { answers: String ->
                    answers.fold(mutableSetOf<Char>()) { acc, answer: Char -> acc.add(answer); acc }
                }
        }

    val answer1: Int = groups
        .map { persons: List<Set<Char>> ->
            persons.fold(mutableSetOf<Char>()) { acc, answers: Set<Char> -> acc.addAll(answers); acc }
        }
        .fold(0) { acc, list -> acc + list.size }

    val answer2: Int = groups
        .map { persons: List<Set<Char>> ->
            persons
                .fold(mutableSetOf<Char>()) { acc, answers: Set<Char> -> acc.addAll(answers); acc }
                .filter { answer: Char -> persons.all { answers: Set<Char> -> answers.contains(answer)} }
        }
        .fold(0) { acc, list -> acc + list.size }

    println("--- Day 6: Custom Customs ---")
    println(answer1)
    println("--- Part Two ---")
    println(answer2)
}
