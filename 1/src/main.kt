import java.io.File

typealias Solution = Set<Int>
typealias NumberSet = Set<Int>

const val TARGET_SUM = 2020

fun NumberSet.findSolution(solutionSize: Int) = this.findSolution(mutableSetOf(), solutionSize)

fun NumberSet.findSolution(acc: MutableSet<Int>, solutionSize: Int): Solution? {
    if (acc.size >= solutionSize) {
        return when {
            acc.sum() == TARGET_SUM -> acc
            else -> null
        }
    }

    if (acc.sum() > TARGET_SUM) return null

    for (number in this) {
        if (acc.contains(number)) continue
        acc.add(number)
        val possibleSolution = this.findSolution(acc, solutionSize)
        if (possibleSolution != null) {
            return possibleSolution
        } else {
            acc.remove(number)
        }
    }

    return null
}

val Solution.answer: Int
    get() = this.fold(1) { acc, number -> acc * number }

fun String.parseNumbers(): NumberSet = this
    .split('\n')
    .map { it.toInt() }
    .toSet()

fun main() {
    val data: String = File("input.txt").readText()
    val numbers: NumberSet = data.parseNumbers()

    println("--- Day 1: Report Repair ---")
    println(numbers.findSolution(solutionSize = 2)?.answer)
    println("--- Part Two ---")
    println(numbers.findSolution(solutionSize = 3)?.answer)
}
