import java.io.File

class PasswordWithRule(val rule: Rule, val password: String) {
    fun isValid1(): Boolean = this.rule.isValid1(this.password)
    fun isValid2(): Boolean = this.rule.isValid2(this.password)
}

class Rule(val num1: Int, val num2: Int, val character: Char) {
    fun isValid1(password: String): Boolean {
        val count = password.count { it == this.character }
        return count in this.num1..this.num2
    }

    fun isValid2(password: String): Boolean {
        val index1 = this.num1 - 1
        val index2 = this.num2 - 1
        return try {
            (password[index1] == this.character) != (password[index2] == this.character)
        } catch (e: Exception) {
            false
        }
    }
}

fun String.parseRule(): Rule {
    val splits = this.split(' ', '-')
    return Rule(splits[0].toInt(), splits[1].toInt(), splits[2].first())
}

fun String.parsePasswordWithRule(): PasswordWithRule {
    val splits = this.split(": ")
    val rule: Rule = splits[0].parseRule()
    val password: String = splits[1]
    return PasswordWithRule(rule, password)
}

fun String.parsePasswordsWithRule(): List<PasswordWithRule> = this
    .split('\n')
    .map { it.parsePasswordWithRule() }

fun main() {
    val data: String = File("input.txt").readText()
    val passwordsWithRule: List<PasswordWithRule> = data.parsePasswordsWithRule()

    println("--- Day 2: Password Philosophy ---")
    println(passwordsWithRule.count { it.isValid1() })

    println("--- Part Two ---")
    println(passwordsWithRule.count { it.isValid2() })
}
