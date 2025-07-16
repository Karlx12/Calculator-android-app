package hrcode.labs.calculator.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import hrcode.labs.calculator.logic.CalculatorLogic
import hrcode.labs.calculator.model.CalculatorDatabase
import hrcode.labs.calculator.model.CalculatorOperation
import hrcode.labs.calculator.model.HistoryEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

open class CalculatorViewModel(app: Application) : AndroidViewModel(app) {
    private val db = CalculatorDatabase.getDatabase(app)
    private val dao = db.historyDao()

    private val _expression = MutableLiveData("")
    open val expression: LiveData<String> = _expression

    private val _result = MutableLiveData("")
    val result: LiveData<String> = _result

    open val history: StateFlow<List<HistoryEntity>> = dao.getAllHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onNumberClick(number: String) {
        _expression.value = (_expression.value ?: "") + number
    }

    fun onOperationClick(operation: CalculatorOperation) {
        when (operation) {
            is CalculatorOperation.ParenthesisOpen -> _expression.value = (_expression.value ?: "") + "("
            is CalculatorOperation.ParenthesisClose -> _expression.value = (_expression.value ?: "") + ")"
            is CalculatorOperation.ChangeSign -> {
                val regex = Regex("(-?\\d+\\.?\\d*)$")
                val match = regex.find(_expression.value ?: "")
                if (match != null) {
                    val number = match.value
                    val newNumber = if (number.startsWith("-")) number.drop(1) else "-$number"
                    _expression.value = (_expression.value ?: "").dropLast(number.length) + newNumber
                }
            }
            else -> _expression.value = (_expression.value ?: "") + operation.symbol
        }
    }

    fun onClear() {
        _expression.value = ""
        _result.value = ""
    }

    fun onErase() {
        val expr = _expression.value ?: ""
        if (expr.isNotEmpty()) {
            // Remove only the last character
            val newExpr = expr.dropLast(1)
            _expression.value = newExpr
            // Optionally clear result if expression is empty
            if (newExpr.isEmpty()) {
                _result.value = ""
            }
        }
    }

    fun onCalculate() {
        val expr = _expression.value ?: ""
        try {
            val res = CalculatorLogic.evaluateExpression(expr)
            _result.value = res.toString()
            viewModelScope.launch {
                dao.insertHistory(HistoryEntity(expression = expr, result = res.toString()))
            }
        } catch (e: Exception) {
            _result.value = "Error"
        }
    }

    fun setExpression(newExpr: String) {
        _expression.value = newExpr
    }

    fun insertAtCursor(text: String, position: Int) {
        val expr = _expression.value ?: ""
        val newExpr = expr.substring(0, position) + text + expr.substring(position)
        _expression.value = newExpr
    }

    fun eraseAtCursor(position: Int) {
        val expr = _expression.value ?: ""
        if (expr.isNotEmpty() && position < expr.length) {
            val newExpr = expr.removeRange(position, position + 1)
            _expression.value = newExpr
        }
        if ((_expression.value ?: "").isEmpty()) {
            _result.value = ""
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            dao.clearHistory()
        }
    }
}

class CalculatorViewModelFactory(private val app: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalculatorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CalculatorViewModel(app) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
