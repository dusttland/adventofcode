import java.io.File
import java.security.InvalidParameterException
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
    abstract var position: Position
        protected set

    infix fun apply(instructions: List<Instruction>) = instructions.forEach { this apply it }

    infix fun apply(instruction: Instruction) {
        when (instruction.prefix) {
            'N', 'E', 'S', 'W' -> this.direction(instruction.prefix.toDirection(), instruction.value)
            'F' -> this.forward(instruction.value)
            'R' -> this.right(instruction.value / 90)
            'L' -> this.left(instruction.value / 90)
        }
    }

    abstract fun forward(count: Int)
    abstract fun direction(direction: Direction, count: Int)
    abstract fun left(count: Int)
    abstract fun right(count: Int)
}

class SimpleShip(
    override var position: Position,
    private var faceDirection: Direction
) : Ship() {
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
    override var position: Position,
    private var waypointPosition: Position
) : Ship() {
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

fun Char.toDirection(): Direction = when (this) {
    'N' -> Direction.NORTH
    'E' -> Direction.EAST
    'S' -> Direction.SOUTH
    'W' -> Direction.WEST
    else -> throw InvalidParameterException("'$this' is not a direction.")
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

    val ship1: Ship = SimpleShip(startPosition, Direction.EAST)
    ship1 apply instructions

    val ship2: Ship = WaypointShip(startPosition, waypointPosition)
    ship2 apply instructions

    println("Part 1: ${startPosition manhattanDistanceTo ship1.position}")
    println("Part 2: ${startPosition manhattanDistanceTo ship2.position}")
}
