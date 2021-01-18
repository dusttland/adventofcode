import java.io.File

typealias Passport = Map<String, String>

class FieldWithValidator(val field: String, private val validator: (String) -> Boolean) {
    fun isValid(value: String): Boolean = tryOrFalse { this.validator.invoke(value) }
}

val EYE_COLORS = setOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth")

val REQUIRED_FIELDS: List<FieldWithValidator> = listOf(
    "byr" validWhen { it.toInt() in 1920..2002 },
    "iyr" validWhen { it.toInt() in 2010..2020 },
    "eyr" validWhen { it.toInt() in 2020..2030 },
    "ecl" validWhen { it in EYE_COLORS },
    "pid" validWhen { it matches "[0-9]{9}" },
    "hcl" validWhen { it matches "#[a-f0-9]{6}" },
    "hgt" validWhen {
        when {
            it.contains("cm") -> it.removeSuffix("cm").toInt() in 150..193
            it.contains("in") -> it.removeSuffix("in").toInt() in 59..76
            else -> false
        }
    }
)

inline fun tryOrFalse(operation: () -> Boolean): Boolean = try { operation() } catch (e: Exception) { false }

infix fun String.validWhen(validator: (String) -> Boolean) = FieldWithValidator(this, validator)

infix fun String.matches(regexString: String) = regexString.toRegex().matches(this)

infix fun FieldWithValidator.isValidIn(passport: Passport): Boolean {
    val value = passport[this.field] ?: return false
    return this.isValid(value)
}

fun Passport.containsAllRequiredFields(): Boolean = REQUIRED_FIELDS.all { it.field in this }

fun Passport.isValid(): Boolean = REQUIRED_FIELDS.all { it isValidIn this }

fun String.parseParameter(): Pair<String, String> {
    val values = this.split(':')
    return values[0] to values[1]
}

fun String.parsePassportCandidate(): Passport = this
    .split(' ', '\n')
    .filter { it.isNotBlank() }
    .map { it.parseParameter() }
    .toMap()

fun String.parsePassportCandidates(): List<Passport> = this
    .split("\n\n")
    .map { it.parsePassportCandidate() }

fun main() {
    val passports: List<Passport> = File("input.txt").readText().parsePassportCandidates()
    println("Part 1: ${passports.count { it.containsAllRequiredFields() }}")
    println("Part 2: ${passports.count { it.isValid() }}")
}
