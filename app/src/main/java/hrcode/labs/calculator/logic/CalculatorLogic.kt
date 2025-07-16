package hrcode.labs.calculator.logic

object CalculatorLogic {
    // Simple expression evaluator supporting +, -, *, /, %, parentheses
    fun evaluateExpression(expr: String): Double {
        // Shunting Yard algorithm for parsing and evaluating
        val output = mutableListOf<String>()
        val operators = mutableListOf<Char>()
        val precedence = mapOf(
            '+' to 1,
            '-' to 1,
            '*' to 2,
            '/' to 2,
            '%' to 2
        )
        var i = 0
        while (i < expr.length) {
            when (val c = expr[i]) {
                in '0'..'9', '.' -> {
                    val start = i
                    while (i < expr.length && (expr[i] in '0'..'9' || expr[i] == '.')) i++
                    output.add(expr.substring(start, i))
                    continue
                }
                '+', '-', '*', '/', '%' -> {
                    while (operators.isNotEmpty() && operators.last() != '(' &&
                        precedence[operators.last()]!! >= precedence[c]!!) {
                        output.add(operators.removeAt(operators.size - 1).toString())
                    }
                    operators.add(c)
                }
                '(' -> operators.add(c)
                ')' -> {
                    while (operators.isNotEmpty() && operators.last() != '(') {
                        output.add(operators.removeAt(operators.size - 1).toString())
                    }
                    if (operators.isNotEmpty() && operators.last() == '(') {
                        operators.removeAt(operators.size - 1)
                    }
                }
            }
            i++
        }
        while (operators.isNotEmpty()) {
            output.add(operators.removeAt(operators.size - 1).toString())
        }
        // Evaluate RPN
        val stack = mutableListOf<Double>()
        for (token in output) {
            when (token) {
                "+" -> {
                    val b = stack.removeAt(stack.size - 1)
                    val a = stack.removeAt(stack.size - 1)
                    stack.add(a + b)
                }
                "-" -> {
                    val b = stack.removeAt(stack.size - 1)
                    val a = stack.removeAt(stack.size - 1)
                    stack.add(a - b)
                }
                "*" -> {
                    val b = stack.removeAt(stack.size - 1)
                    val a = stack.removeAt(stack.size - 1)
                    stack.add(a * b)
                }
                "/" -> {
                    val b = stack.removeAt(stack.size - 1)
                    val a = stack.removeAt(stack.size - 1)
                    stack.add(a / b)
                }
                "%" -> {
                    val b = stack.removeAt(stack.size - 1)
                    val a = stack.removeAt(stack.size - 1)
                    stack.add(a % b)
                }
                else -> stack.add(token.toDouble())
            }
        }
        return stack.lastOrNull() ?: Double.NaN
    }
}

