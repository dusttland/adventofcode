import java.io.File
import kotlin.math.abs

enum class Direction {
    NORTH, EAST, SOUTH, WEST;

    val left: Direction
        get() = when (this) {
            NORTH -> WEST
            EAST -> NORTH
            SOUTH -> EAST
            WEST -> SOUTH
        }

    val right: Direction
        get() = when (this) {
            NORTH -> EAST
            EAST -> SOUTH
            SOUTH -> WEST
            WEST -> NORTH
        }
}

enum class Move { FORWARD, RIGHT, LEFT }

data class Position(val x: Int, val y: Int) {
    operator fun plus(other: Position) = Position(this.x + other.x, this.y + other.y)
    operator fun minus(other: Position) = Position(this.x - other.x, this.y - other.y)
    operator fun times(number: Int) = Position(this.x * number, this.y * number)

    infix fun manhattanDistanceTo(other: Position): Int {
        val pos = (this - other).abs()
        return pos.x + pos.y
    }

    fun rotateLeftAroundOrigin() = Position(this.y, -this.x)

    fun rotateRightAroundOrigin() = Position(-this.y, this.x)

    private fun abs() = Position(abs(this.x), abs(this.y))
}

data class Instruction(val prefix: Char, val value: Int)

abstract class Ship {
    infix fun apply(instruction: Instruction) {
        val direction: Direction? = instruction.prefix.toDirectionOrNull()
        val move: Move? = instruction.prefix.toMoveOrNull()
        when {
            direction != null -> this.direction(direction, instruction.value)
            move != null -> when (move) {
                Move.FORWARD -> this.forward(instruction.value)
                Move.RIGHT -> this.right(instruction.value / 90)
                Move.LEFT -> this.left(instruction.value / 90)
            }
        }
    }

    abstract fun forward(count: Int)
    abstract fun direction(direction: Direction, count: Int)
    abstract fun left(count: Int)
    abstract fun right(count: Int)
}

open class SimpleShip(
    position: Position,
    private var faceDirection: Direction
) : Ship() {
    var position: Position = position
        private set

    override fun forward(count: Int) {
        this.direction(this.faceDirection, count)
    }

    override fun direction(direction: Direction, count: Int) {
        val posChange = direction.toPosition() * count
        this.position += posChange
    }

    override fun left(count: Int) = repeat(count) { this.faceDirection = this.faceDirection.left }

    override fun right(count: Int) = repeat(count) { this.faceDirection = this.faceDirection.right }
}

class WaypointShip(
    position: Position,
    private var waypointPosition: Position
) : Ship() {
    var position: Position = position
        private set

    override fun forward(count: Int) {
        val posChange = this.waypointPosition * count
        this.position += posChange
    }

    override fun direction(direction: Direction, count: Int) {
        val posChange = direction.toPosition() * count
        this.waypointPosition += posChange
    }

    override fun left(count: Int) {
        repeat(count) { this.waypointPosition = this.waypointPosition.rotateLeftAroundOrigin() }
    }

    override fun right(count: Int) {
        repeat(count) { this.waypointPosition = this.waypointPosition.rotateRightAroundOrigin() }
    }
}

fun Char.toMoveOrNull(): Move? = when (this) {
    'F' -> Move.FORWARD
    'L' -> Move.LEFT
    'R' -> Move.RIGHT
    else -> null
}

fun Char.toDirectionOrNull(): Direction? = when (this) {
    'N' -> Direction.NORTH
    'E' -> Direction.EAST
    'S' -> Direction.SOUTH
    'W' -> Direction.WEST
    else -> null
}

fun Direction.toPosition() = when (this) {
    Direction.NORTH -> Position(0, -1)
    Direction.EAST -> Position(1, 0)
    Direction.SOUTH -> Position(0, 1)
    Direction.WEST -> Position(-1, 0)
}

fun String.parseInstruction() = Instruction(this.first(), this.substring(1).toInt())

fun String.parseInstructions(): List<Instruction> = this
    .split('\n')
    .map { it.trim().parseInstruction() }

fun main() {
    val data: String = File("input.txt").readText()
    val instructions: List<Instruction> = data.parseInstructions()
    val startPosition = Position(0, 0)
    val waypointPosition = Position(10, -1)

    val ship1 = SimpleShip(startPosition, Direction.EAST)
    instructions.forEach { ship1 apply it }
    println("Part 1: ${startPosition manhattanDistanceTo ship1.position}")

    val ship2 = WaypointShip(startPosition, waypointPosition)
    instructions.forEach { ship2 apply it }
    println("Part 2: ${startPosition manhattanDistanceTo ship2.position}")
}
