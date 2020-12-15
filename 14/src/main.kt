import java.io.File

abstract class Operation
class MaskOperation(val mask: String) : Operation()
class MemoryOperation(val address: Long, val value: Long) : Operation()

typealias Program = List<Operation>
typealias Memory = MutableMap<Long, Long>

infix fun Long.maskedWith(mask: String): Long {
    var newValue = this
    var bitCursor: Long = 1L
    mask.reversed().forEach {
        when (it) {
            '1' -> newValue = bitCursor or newValue
            '0' -> newValue = bitCursor.inv() and newValue
        }
        bitCursor = bitCursor shl 1
    }
    return newValue
}

fun Long.bitsAsBooleanList(listSize: Int): List<Boolean> {
    val bitList: MutableList<Boolean> = mutableListOf()
    for (i in 0 until listSize) {
        val bit = 1L shl i
        val isSet = this and bit != 0L
        bitList.add(isSet)
    }
    return bitList
}

fun List<Long>.bitVariation(number: Long): List<Pair<Long, Boolean>> {
    val bitList: List<Boolean> = number.bitsAsBooleanList(this.size)
    return this.zip(bitList)
}

fun Long.applyVariation(variation: List<Pair<Long, Boolean>>): Long {
    var newValue = this
    variation.forEach {
        val bit: Long = it.first
        val isSet: Boolean = it.second
        when (isSet) {
            true -> newValue = bit or newValue
            false -> newValue = bit.inv() and newValue
        }
    }
    return newValue
}

fun Long.allFloatingAddresses(mask: String): List<Long> {
    val addresses: MutableList<Long> = mutableListOf()
    val floatingBits: MutableList<Long> = mutableListOf()
    var newValue = this
    mask.reversed().forEachIndexed { index, char ->
        val bit = 1L shl index
        when (char) {
            '1' -> newValue = bit or newValue
            'X' -> floatingBits.add(bit)
        }
    }

    val addressCount: Int = 1 shl floatingBits.size
    for (variationNumber in 0 until addressCount) {
        val variation: List<Pair<Long, Boolean>> = floatingBits.bitVariation(variationNumber.toLong())
        val newAddress = newValue.applyVariation(variation)
        addresses.add(newAddress)
    }

    return addresses
}

fun Program.firstMemory(): Memory {
    val memory: Memory = mutableMapOf()
    var mask: String = ""
    this.forEach { op ->
        when (op) {
            is MaskOperation -> mask = op.mask
            is MemoryOperation -> memory[op.address] = op.value maskedWith mask
        }
    }
    return memory
}

fun Program.secondMemory(): Memory {
    val memory: Memory = mutableMapOf()
    var mask: String = ""
    this.forEach { op ->
        when (op) {
            is MaskOperation -> mask = op.mask
            is MemoryOperation -> {
                val floatingAddresses = op.address.allFloatingAddresses(mask)
                floatingAddresses.forEach { memory[it] = op.value }
            }
        }
    }
    return memory
}

val Memory.answer: Long
    get() = this.toList().sumOf { it.second }

fun String.parseOperation(): Operation = when {
    this.startsWith("mask = ") -> MaskOperation(this.removePrefix("mask = "))
    else -> {
        val data = this.removePrefix("mem[")
        val splits = data.split("] = ")
        val address = splits[0].toLong()
        val value = splits[1].toLong()
        MemoryOperation(address, value)
    }
}

fun String.parseProgram(): Program = this
    .split('\n')
    .map { it.parseOperation() }

fun main() {
    val data: String = File("input.txt").readText()
    val program: Program = data.parseProgram()
    println("Part 1: ${program.firstMemory().answer}")
    println("Part 2: ${program.secondMemory().answer}")
}
