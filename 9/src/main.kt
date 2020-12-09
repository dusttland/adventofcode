import java.io.File

fun List<Long>.doesSumExist(sum: Long): Boolean = this.any { number ->
    this.any { otherNumber -> otherNumber != number && number + otherNumber == sum }
}

fun List<Long>.findFirstNotSummableNumber(preamble: Int): Long? {
    for (index in preamble until this.size) {
        val precedingNumbers = this.subList(index - preamble, index)
        val sumToLookFor = this[index]
        if (!precedingNumbers.doesSumExist(sumToLookFor))
            return sumToLookFor
    }
    return null
}

fun List<Long>.findContiguousNumbersForSum(targetSum: Long): List<Long>? {
    for (startIndex in this.indices) {
        var currentSum: Long = 0
        for (endIndex in startIndex until this.size) {
            currentSum += this[endIndex]
            when {
                currentSum == targetSum -> return this.subList(startIndex, endIndex + 1)
                currentSum > targetSum -> break
            }
        }
    }
    return null
}

fun List<Long>.minMaxSum(): Long? {
    val min = this.minOrNull() ?: return null
    val max = this.maxOrNull() ?: return null
    return min + max
}

fun String.parseNumbers(): List<Long> = this
    .split('\n')
    .map { it.trim().toLong() }

fun main() {
    val data: String = File("input.txt").readText()
    val numbers: List<Long> = data.parseNumbers()
    val preambleNumberCount = 25

    println("--- Day 9: Encoding Error ---")
    val number: Long = numbers.findFirstNotSummableNumber(preambleNumberCount) ?: return
    println(number)

    println("--- Part Two ---")
    val contiguousNumbers = numbers.findContiguousNumbersForSum(number) ?: return
    println(contiguousNumbers.minMaxSum())
}
