const val ONE_HUNDRED = 100
const val ONE_MILLION = 1000000
const val TEN_MILLION = 10000000

typealias Cups = TailoredLinkedList<Int>

class Node<T>(
    val value: T,
    var next: Node<T>? = null,
    var previousValueNode: Node<T>? = null
)

class TailoredLinkedList<T> : Iterable<T> {
    var head: Node<T>? = null
    var tail: Node<T>? = null

    override fun toString(): String {
        var str = "["
        var node = this.head
        while (node != null) {
            str += "${node.value}, "
            node = node.next
        }
        return str.removeSuffix(", ") + "]"
    }

    fun firstNode(): Node<T> = this.head!!

    operator fun contains(value: T): Boolean = this.indexOf(value) >= 0

    fun append(value: T) {
        val node = Node(value)
        this.appendNode(node)
    }

    fun appendNode(node: Node<T>) {
        node.next = null
        val tail = this.tail
        if (tail == null) {
            this.head = node
            this.tail = node
        } else {
            tail.next = node
            this.tail = node
        }
    }

    fun pop(): T = this.popNode().value

    fun popNode(): Node<T> {
        val head = this.head!!
        this.head = head.next
        head.next = null
        return head
    }

    fun shiftLeft() {
        val firstNode: Node<T> = this.popNode()
        this.appendNode(firstNode)
    }

    fun popSubList(from: Int, to: Int): TailoredLinkedList<T> {
        var index = 0
        var previousNode: Node<T>? = null
        var currentNode = this.head
        var preHead: Node<T>? = null
        var head: Node<T>? = null
        var tail: Node<T>? = null
        while (index <= to && currentNode != null) {
            if (index == from) {
                preHead = previousNode
                head = currentNode
            }
            if (index == to - 1) {
                tail = currentNode
            }
            previousNode = currentNode
            currentNode = currentNode.next
            index++
        }

        val lst = TailoredLinkedList<T>()
        if (head != null && tail != null) {
            if (preHead != null) {
                preHead.next = tail.next
            } else {
                this.head = tail.next
            }
            tail.next = null

            lst.head = head
            lst.tail = tail
        }
        return lst
    }

    fun nodeOf(value: T): Node<T>? {
        var node: Node<T>? = this.head
        while (node != null && node.value != value) {
            node = node.next
        }
        return node
    }

    fun insertAfterNode(lst: TailoredLinkedList<T>, node: Node<T>) {
        val tail = node.next
        node.next = lst.head
        if (tail == null) {
            this.tail = lst.tail
        } else {
            lst.tail?.next = tail
        }
    }

    fun indexOf(value: T): Int {
        var index = 0
        var node: Node<T>? = this.head
        while (node != null) {
            if (node.value == value) return index
            node = node.next
            index++
        }
        return -1
    }

    override fun iterator(): Iterator<T> = LinkedListIterator(this.head)

    class LinkedListIterator<T>(private var node: Node<T>?) : Iterator<T> {
        override fun hasNext(): Boolean = this.node != null
        override fun next(): T {
            val value = this.node!!.value
            this.node = this.node!!.next
            return value
        }
    }

    fun nodeIterator(): Iterator<Node<T>> = NodeIterator(this.head)

    class NodeIterator<T>(private var node: Node<T>?) : Iterator<Node<T>> {
        override fun hasNext(): Boolean = this.node != null
        override fun next(): Node<T> {
            val node = this.node!!
            this.node = node.next
            return node
        }
    }

}

fun <T> List<T>.toLinkedList(): TailoredLinkedList<T> {
    val lst = TailoredLinkedList<T>()
    this.forEach { lst.append(it) }
    return lst
}

fun List<Int>.millionizedCups(): Cups {
    val maxValue = this.maxOrNull()!!
    val cups = mutableListOf<Int>()
    cups.addAll(this)
    repeat(ONE_MILLION - maxValue) {
        cups.add(it + 1 + maxValue)
    }
    return cups.toLinkedList()
}

fun String.constructFirstCups(): Cups {
    val cups: Cups = this.toIntList().toLinkedList()
    val maxValue: Int = cups.maxOrNull()!!
    val iterator = cups.nodeIterator()
    while (iterator.hasNext()) {
        val node = iterator.next()
        val previousValueNode = if (node.value - 1 < 1) {
            cups.nodeOf(maxValue)
        } else {
            cups.nodeOf(node.value - 1)
        }
        node.previousValueNode = previousValueNode
    }
    return cups
}

fun String.constructSecondCups(): Cups {
    val input: List<Int> = this.toIntList()
    val cups: Cups = input.millionizedCups()
    val maxValueInInput: Int = input.maxOrNull()!!

    val iterator = cups.nodeIterator()
    repeat(input.size) {
        val node = iterator.next()
        node.previousValueNode = when {
            node.value - 1 > 0 -> cups.nodeOf(node.value - 1)
            else -> cups.tail
        }
    }

    var previousNode = cups.nodeOf(maxValueInInput)
    while (iterator.hasNext()) {
        val node = iterator.next()
        node.previousValueNode = previousNode
        previousNode = node
    }
    return cups
}

fun Cups.crabShuffle() {
    val pickedCups: Cups = this.popSubList(1, 4)

    var destinationNode: Node<Int>? = this.firstNode()
    do {
        destinationNode = destinationNode?.previousValueNode
    } while (destinationNode != null && destinationNode.value in pickedCups)

    when {
        destinationNode != null -> this.insertAfterNode(pickedCups, destinationNode)
        else -> throw Exception("Destination node is null.")
    }

    this.shiftLeft()
}

fun Cups.crabShuffle(iterations: Int) {
    repeat(iterations) { this.crabShuffle() }
}

fun Cups.shiftOneToFirstPosition() {
    while (this.firstNode().value != 1) this.shiftLeft()
}

fun Iterable<Int>.multiplied(): Long = this.fold(1L) { acc, i -> acc * i }

fun String.toIntList(): List<Int> = this.map { it - '0' }

fun main() {
    val input = "589174263"

    val cups1: Cups = input.constructFirstCups()
    cups1.crabShuffle(iterations = ONE_HUNDRED)
    cups1.shiftOneToFirstPosition()
    cups1.pop()
    println("Part 1: ${cups1.fold("") { acc, i -> "$acc$i" }}")

    val cups2: Cups = input.constructSecondCups()
    cups2.crabShuffle(iterations = TEN_MILLION)
    cups2.shiftOneToFirstPosition()
    println("Part 2: ${cups2.popSubList(1, 3).multiplied()}")
}
