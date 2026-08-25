package com.example.engine

import com.example.model.AngleMode
import com.example.model.Language
import com.example.util.AppStrings
import com.example.util.NumberFormatter
import java.util.Stack
import kotlin.math.*

sealed class EvalResult {
    data class Success(val value: Double, val formatted: String) : EvalResult()
    data class Error(val messageKey: String, val rawMessage: String? = null) : EvalResult()
}

object CalculatorEngine {

    const val PI_VAL = Math.PI
    const val E_VAL = Math.E
    const val EULER_VAL = 0.5772156649015328606065120900824024310421

    fun evaluate(
        expression: String,
        angleMode: AngleMode = AngleMode.DEG,
        precision: Int = 8,
        useThousandsSeparator: Boolean = true
    ): EvalResult {
        if (expression.isBlank()) {
            return EvalResult.Success(0.0, "0")
        }

        try {
            val sanitized = sanitize(expression)
            val tokens = tokenize(sanitized)
            val rpn = toRpn(tokens)
            val value = evaluateRpn(rpn, angleMode)

            if (value.isNaN()) {
                return EvalResult.Error("error_domain")
            }
            if (value.isInfinite()) {
                return EvalResult.Error("error_division_by_zero")
            }

            val cleaned = NumberFormatter.cleanNumber(value)
            val formatted = NumberFormatter.formatResult(cleaned, precision, useThousandsSeparator)
            return EvalResult.Success(cleaned, formatted)
        } catch (e: ArithmeticException) {
            return EvalResult.Error("error_division_by_zero", e.message)
        } catch (e: IllegalArgumentException) {
            return EvalResult.Error("error_invalid_input", e.message)
        } catch (e: Exception) {
            return EvalResult.Error("error_syntax", e.message)
        }
    }

    private fun sanitize(input: String): String {
        return input
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace("π", PI_VAL.toString())
            .replace("e", E_VAL.toString())
            .replace("γ", EULER_VAL.toString())
            .replace("√", "sqrt")
            .replace("∛", "cbrt")
            .replace(" ", "")
    }

    private fun tokenize(expr: String): List<String> {
        val tokens = mutableListOf<String>()
        var i = 0
        val len = expr.length

        while (i < len) {
            val c = expr[i]

            // Number reading (including decimal dot)
            if (c.isDigit() || c == '.') {
                val sb = StringBuilder()
                while (i < len && (expr[i].isDigit() || expr[i] == '.' || expr[i] == 'E' || expr[i] == 'e')) {
                    if ((expr[i] == 'E' || expr[i] == 'e') && i + 1 < len && (expr[i + 1] == '+' || expr[i + 1] == '-')) {
                        sb.append(expr[i])
                        i++
                        sb.append(expr[i])
                        i++
                    } else {
                        sb.append(expr[i])
                        i++
                    }
                }
                tokens.add(sb.toString())
                continue
            }

            // Word functions: sin, cos, tan, sinh, cosh, tanh, asin, acos, atan, log, ln, sqrt, cbrt, abs, fact, mod
            if (c.isLetter()) {
                val sb = StringBuilder()
                while (i < len && expr[i].isLetter()) {
                    sb.append(expr[i])
                    i++
                }
                val word = sb.toString().lowercase()
                tokens.add(word)
                continue
            }

            // Single char operators & symbols
            when (c) {
                '+', '-', '*', '/', '%', '^', '(', ')' -> {
                    // Check for unary minus: at start or right after another operator or '('
                    if (c == '-') {
                        val prevToken = tokens.lastOrNull()
                        val isUnary = prevToken == null ||
                                prevToken in listOf("+", "-", "*", "/", "%", "^", "(", "mod")
                        if (isUnary) {
                            tokens.add("neg")
                            i++
                            continue
                        }
                    }
                    tokens.add(c.toString())
                    i++
                }
                '!' -> {
                    tokens.add("fact")
                    i++
                }
                else -> {
                    i++
                }
            }
        }
        return tokens
    }

    private fun isOperator(t: String): Boolean =
        t in listOf("+", "-", "*", "/", "%", "^", "mod", "neg")

    private fun isFunction(t: String): Boolean =
        t in listOf(
            "sin", "cos", "tan", "sinh", "cosh", "tanh",
            "asin", "acos", "atan", "log", "ln", "sqrt", "cbrt",
            "abs", "fact", "sqr", "cube", "inv"
        )

    private fun precedence(op: String): Int = when (op) {
        "+", "-" -> 1
        "*", "/", "%", "mod" -> 2
        "neg" -> 3
        "^" -> 4
        else -> 0
    }

    private fun isRightAssociative(op: String): Boolean = op == "^" || op == "neg"

    private fun toRpn(tokens: List<String>): List<String> {
        val output = mutableListOf<String>()
        val stack = Stack<String>()

        for (token in tokens) {
            when {
                token.toDoubleOrNull() != null -> {
                    output.add(token)
                }
                isFunction(token) -> {
                    stack.push(token)
                }
                token == "(" -> {
                    stack.push(token)
                }
                token == ")" -> {
                    while (stack.isNotEmpty() && stack.peek() != "(") {
                        output.add(stack.pop())
                    }
                    if (stack.isNotEmpty() && stack.peek() == "(") {
                        stack.pop() // remove '('
                    }
                    if (stack.isNotEmpty() && isFunction(stack.peek())) {
                        output.add(stack.pop())
                    }
                }
                isOperator(token) -> {
                    while (stack.isNotEmpty() && isOperator(stack.peek())) {
                        val top = stack.peek()
                        val pCurrent = precedence(token)
                        val pTop = precedence(top)
                        if ((!isRightAssociative(token) && pCurrent <= pTop) ||
                            (isRightAssociative(token) && pCurrent < pTop)
                        ) {
                            output.add(stack.pop())
                        } else {
                            break
                        }
                    }
                    stack.push(token)
                }
            }
        }

        while (stack.isNotEmpty()) {
            val top = stack.pop()
            if (top != "(" && top != ")") {
                output.add(top)
            }
        }

        return output
    }

    private fun evaluateRpn(rpn: List<String>, angleMode: AngleMode): Double {
        val stack = Stack<Double>()

        for (token in rpn) {
            val num = token.toDoubleOrNull()
            if (num != null) {
                stack.push(num)
                continue
            }

            when (token) {
                "neg" -> {
                    if (stack.isEmpty()) throw IllegalArgumentException()
                    stack.push(-stack.pop())
                }
                "+" -> {
                    if (stack.size < 2) throw IllegalArgumentException()
                    val b = stack.pop()
                    val a = stack.pop()
                    stack.push(a + b)
                }
                "-" -> {
                    if (stack.size < 2) throw IllegalArgumentException()
                    val b = stack.pop()
                    val a = stack.pop()
                    stack.push(a - b)
                }
                "*" -> {
                    if (stack.size < 2) throw IllegalArgumentException()
                    val b = stack.pop()
                    val a = stack.pop()
                    stack.push(a * b)
                }
                "/" -> {
                    if (stack.size < 2) throw IllegalArgumentException()
                    val b = stack.pop()
                    val a = stack.pop()
                    if (abs(b) < 1e-15) throw ArithmeticException("Division by zero")
                    stack.push(a / b)
                }
                "%", "mod" -> {
                    if (stack.size < 2) throw IllegalArgumentException()
                    val b = stack.pop()
                    val a = stack.pop()
                    if (abs(b) < 1e-15) throw ArithmeticException("Division by zero")
                    stack.push(a % b)
                }
                "^" -> {
                    if (stack.size < 2) throw IllegalArgumentException()
                    val b = stack.pop()
                    val a = stack.pop()
                    stack.push(a.pow(b))
                }
                "sin" -> {
                    if (stack.isEmpty()) throw IllegalArgumentException()
                    val a = stack.pop()
                    val rad = if (angleMode == AngleMode.DEG) Math.toRadians(a) else a
                    val res = sin(rad)
                    stack.push(if (abs(res) < 1e-15) 0.0 else res)
                }
                "cos" -> {
                    if (stack.isEmpty()) throw IllegalArgumentException()
                    val a = stack.pop()
                    val rad = if (angleMode == AngleMode.DEG) Math.toRadians(a) else a
                    val res = cos(rad)
                    stack.push(if (abs(res) < 1e-15) 0.0 else res)
                }
                "tan" -> {
                    if (stack.isEmpty()) throw IllegalArgumentException()
                    val a = stack.pop()
                    val rad = if (angleMode == AngleMode.DEG) Math.toRadians(a) else a
                    if (angleMode == AngleMode.DEG && abs((a - 90.0) % 180.0) < 1e-6) {
                        throw ArithmeticException("Tangent undefined")
                    }
                    val res = tan(rad)
                    stack.push(if (abs(res) < 1e-15) 0.0 else res)
                }
                "sinh" -> {
                    if (stack.isEmpty()) throw IllegalArgumentException()
                    stack.push(sinh(stack.pop()))
                }
                "cosh" -> {
                    if (stack.isEmpty()) throw IllegalArgumentException()
                    stack.push(cosh(stack.pop()))
                }
                "tanh" -> {
                    if (stack.isEmpty()) throw IllegalArgumentException()
                    stack.push(tanh(stack.pop()))
                }
                "asin" -> {
                    if (stack.isEmpty()) throw IllegalArgumentException()
                    val a = stack.pop()
                    if (a < -1.0 || a > 1.0) throw IllegalArgumentException("asin domain [-1, 1]")
                    val rad = asin(a)
                    stack.push(if (angleMode == AngleMode.DEG) Math.toDegrees(rad) else rad)
                }
                "acos" -> {
                    if (stack.isEmpty()) throw IllegalArgumentException()
                    val a = stack.pop()
                    if (a < -1.0 || a > 1.0) throw IllegalArgumentException("acos domain [-1, 1]")
                    val rad = acos(a)
                    stack.push(if (angleMode == AngleMode.DEG) Math.toDegrees(rad) else rad)
                }
                "atan" -> {
                    if (stack.isEmpty()) throw IllegalArgumentException()
                    val a = stack.pop()
                    val rad = atan(a)
                    stack.push(if (angleMode == AngleMode.DEG) Math.toDegrees(rad) else rad)
                }
                "log" -> {
                    if (stack.isEmpty()) throw IllegalArgumentException()
                    val a = stack.pop()
                    if (a <= 0.0) throw IllegalArgumentException("log of non-positive")
                    stack.push(log10(a))
                }
                "ln" -> {
                    if (stack.isEmpty()) throw IllegalArgumentException()
                    val a = stack.pop()
                    if (a <= 0.0) throw IllegalArgumentException("ln of non-positive")
                    stack.push(ln(a))
                }
                "sqrt" -> {
                    if (stack.isEmpty()) throw IllegalArgumentException()
                    val a = stack.pop()
                    if (a < 0.0) throw IllegalArgumentException("sqrt of negative")
                    stack.push(sqrt(a))
                }
                "cbrt" -> {
                    if (stack.isEmpty()) throw IllegalArgumentException()
                    val a = stack.pop()
                    stack.push(Math.cbrt(a))
                }
                "abs" -> {
                    if (stack.isEmpty()) throw IllegalArgumentException()
                    stack.push(abs(stack.pop()))
                }
                "fact" -> {
                    if (stack.isEmpty()) throw IllegalArgumentException()
                    val a = stack.pop()
                    if (a < 0.0 || a % 1.0 != 0.0 || a > 170) throw IllegalArgumentException("Factorial invalid")
                    stack.push(factorial(a.toInt()))
                }
                "sqr" -> {
                    if (stack.isEmpty()) throw IllegalArgumentException()
                    val a = stack.pop()
                    stack.push(a * a)
                }
                "cube" -> {
                    if (stack.isEmpty()) throw IllegalArgumentException()
                    val a = stack.pop()
                    stack.push(a * a * a)
                }
                "inv" -> {
                    if (stack.isEmpty()) throw IllegalArgumentException()
                    val a = stack.pop()
                    if (abs(a) < 1e-15) throw ArithmeticException("Division by zero")
                    stack.push(1.0 / a)
                }
                else -> throw IllegalArgumentException("Unknown token: $token")
            }
        }

        if (stack.size != 1) throw IllegalArgumentException("Invalid expression structure")
        return stack.pop()
    }

    private fun factorial(n: Int): Double {
        if (n <= 1) return 1.0
        var res = 1.0
        for (i in 2..n) {
            res *= i.toDouble()
        }
        return res
    }
}
