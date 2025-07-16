package hrcode.labs.calculator.model

// Represents the available calculator operations
sealed class CalculatorOperation(val symbol: String) {
    object Add : CalculatorOperation("+")
    object Subtract : CalculatorOperation("-")
    object Multiply : CalculatorOperation("*")
    object Divide : CalculatorOperation("/")
    object Percent : CalculatorOperation("%")
    object ParenthesisOpen : CalculatorOperation("(")
    object ParenthesisClose : CalculatorOperation(")")
    object ChangeSign : CalculatorOperation("±")
}

// Represents the calculator's state
 data class CalculatorState(
    val expression: String = "",
    val result: String = ""
)
