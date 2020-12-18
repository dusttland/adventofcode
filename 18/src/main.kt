import java.io.File
import java.security.InvalidParameterException

typealias OperationFunction = (a: Equation, b: Equation) -> Long

interface Equation {
    fun value(): Long
}

data class Operation(
    val a: Equation,
    val func: OperationFunction,
    val b: Equation
) : Equation {
    override fun value(): Long = this.func.invoke(this.a, this.b)
}

data class Value(
    val value: Long
) : Equation {
    override fun value(): Long = this.value
}

fun Char.toOperationFunction(): OperationFunction = when (this) {
    '+' -> { a, b -> a.value() + b.value() }
    '-' -> { a, b -> a.value() - b.value() }
    '*' -> { a, b -> a.value() * b.value() }
    '/' -> { a, b -> a.value() / b.value() }
    else -> throw InvalidParameterException("'$this' is not a valid operation function.")
}

fun String.parseEquation1(): Equation? {
    var cursor = 0
    var lastContentBegin: Int? = null

    var a: Equation? = null
    var b: Equation? = null
    var func: OperationFunction? = null

    while (cursor < this.length) {
        when (this[cursor]) {
            '(' -> {
                var beginCount = 1
                var endCount = 0
                var endCursor = cursor + 1
                while (endCount < beginCount) {
                    when (this[endCursor]) {
                        '(' -> beginCount++
                        ')' -> endCount++
                    }
                    endCursor++
                }
                val operation = this.substring(cursor + 1, endCursor - 1).parseEquation1()
                when (a) {
                    null -> a = operation
                    else -> b = operation
                }
                cursor = endCursor
            }
            ' ' -> {
                if (lastContentBegin != null) {
                    val content = this.substring(lastContentBegin, cursor)
                    when {
                        a == null -> a = Value(content.toLong())
                        func != null -> b = Value(content.toLong())
                        content.length == 1 -> func = content.first().toOperationFunction()
                    }
                }

                cursor++
                lastContentBegin = null
            }
            else -> {
                if (lastContentBegin == null) lastContentBegin = cursor
                cursor++
            }
        }

        if (a != null && b != null && func != null) {
            a = Operation(a, func, b)
            func = null
            b = null
        }
    }

    if (lastContentBegin != null) {
        val content = this.substring(lastContentBegin, cursor)
        if (content.isNotEmpty()) {
            when {
                a == null -> a = Value(content.toLong())
                func != null -> b = Value(content.toLong())
                content.length == 1 -> func = content.first().toOperationFunction()
            }
        }
    }

    if (a != null && b != null && func != null) {
        a = Operation(a, func, b)
    }

    return a
}

fun String.parseEquation2(): Equation? {
    var cursor = 0
    var lastContentBegin: Int? = null

    val operations = mutableListOf<Equation>()
    val functions = mutableListOf<Char>()

    while (cursor < this.length) {
        when (this[cursor]) {
            '(' -> {
                var beginCount = 1
                var endCount = 0
                var endCursor = cursor + 1
                while (endCount < beginCount) {
                    when (this[endCursor]) {
                        '(' -> beginCount++
                        ')' -> endCount++
                    }
                    endCursor++
                }
                val operation = this.substring(cursor + 1, endCursor - 1).parseEquation2()
                if (operation != null) operations.add(operation)
                cursor = endCursor
            }
            ' ' -> {
                if (lastContentBegin != null) {
                    val content = this.substring(lastContentBegin, cursor)
                    when {
                        operations.size == functions.size -> operations.add(Value(content.toLong()))
                        else -> functions.add(content.first())
                    }
                }

                cursor++
                lastContentBegin = null
            }
            else -> {
                if (lastContentBegin == null) lastContentBegin = cursor
                cursor++
            }
        }
    }

    if (lastContentBegin != null) {
        val content = this.substring(lastContentBegin, cursor)
        if (content.isNotEmpty()) {
            operations.add(Value(content.toLong()))
        }
    }

    while (functions.isNotEmpty()) {
        var fIndex = functions.indexOfFirst { it == '+' || it == '-' }
        if (fIndex == -1) fIndex = 0

        val aIndex = fIndex
        val bIndex = fIndex + 1

        val a = operations[aIndex]
        val b = operations[bIndex]
        val f = functions[fIndex].toOperationFunction()

        val operation = Operation(a, f, b)
        operations[aIndex] = operation
        operations.removeAt(bIndex)
        functions.removeAt(fIndex)
    }

    return operations.first()
}

fun String.equationValue1(): Long? = this.parseEquation1()?.value()

fun String.equationValue2(): Long? = this.parseEquation2()?.value()

fun main() {
    val data: String = File("input.txt").readText()
    val equations: List<String> = data.split('\n').map { it.trim() }
    println("Part 1: ${equations.sumOf { it.equationValue1() ?: 0L }}")
    println("Part 2: ${equations.sumOf { it.equationValue2() ?: 0L }}")
}
