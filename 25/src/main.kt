import java.io.File

fun String.parsePublicKeys(): Pair<Int, Int> {
    val splits = this.split('\n')
    return splits[0].toInt() to splits[1].toInt()
}

fun Int.transformOnce(previousValue: Long): Long = this * previousValue % 20201227

fun Int.findLoopSize(target: Int): Int {
    val targetLong = target.toLong()
    var value = 1L
    var loopSize = 0
    while (value != targetLong) {
        value = this.transformOnce(value)
        loopSize++
    }
    return loopSize
}

fun Int.transform(count: Int): Int {
    var value = 1L
    repeat(count) {
        value = this.transformOnce(value)
    }
    return value.toInt()
}

fun main() {
    val keys: Pair<Int, Int> = File("input.txt").readText().parsePublicKeys()
    val secondLoopSize: Int = 7.findLoopSize(keys.second)
    val secretKey: Int = keys.first.transform(secondLoopSize)
    println(secretKey)
}
