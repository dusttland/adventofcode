import java.io.File

typealias PassportCandidate = Map<String, String>
typealias ParameterValidators = Map<String, (String) -> Boolean>

val eyeColors = setOf("amb", "blu", "brn", "gry", "grn", "hzl", "oth")

val requiredParameters: ParameterValidators = mapOf(
    "byr" to { value ->
        try { value.toInt() in 1920..2002 }
        catch (e: Exception) { false }
    },
    "iyr" to { value ->
        try { value.toInt() in 2010..2020 }
        catch (e: Exception) { false }
    },
    "eyr" to { value ->
        try { value.toInt() in 2020..2030 }
        catch (e: Exception) { false }
    },
    "ecl" to { value -> eyeColors.contains(value) },
    "pid" to { value -> Regex("[0-9]{9}").matches(value) },
    "hcl" to { value -> Regex("#[a-f0-9]{6}").matches(value) },
    "hgt" to { value ->
        when {
            value.contains("cm") -> {
                try { value.removeSuffix("cm").toInt() in 150..193 }
                catch (e: Exception) { false }
            }
            value.contains("in") -> {
                try { value.removeSuffix("in").toInt() in 59..76 }
                catch (e: Exception) { false }
            }
            else -> false
        }
    }
)

fun PassportCandidate.isValid(): Boolean {
    for ((paramName, validationFunction) in requiredParameters) {
        val value = this[paramName] ?: return false
        if (!validationFunction.invoke(value)) return false
    }
    return true
}

fun String.parseParameter(): Pair<String, String> {
    val values = this.split(':')
    return values[0] to values[1]
}

fun String.parsePassportCandidate(): PassportCandidate = this
    .split(' ', '\n')
    .filter { it.isNotBlank() }
    .map { it.parseParameter() }
    .toMap()

fun String.parsePassportCandidates(): List<PassportCandidate> = this
    .split("\n\n")
    .map { it.parsePassportCandidate() }

fun main() {
    val data: String = File("input.txt").readText()
    val passports: List<PassportCandidate> = data.parsePassportCandidates()
    val count: Int = passports.count { it.isValid() }
    println(count)
}
