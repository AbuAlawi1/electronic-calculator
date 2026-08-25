package com.example.ui.viewmodel

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.CalculationHistory
import com.example.data.local.entity.CalculationType
import com.example.data.local.entity.FavoriteCalculation
import com.example.data.repository.CalculatorRepository
import com.example.engine.*
import com.example.model.AngleMode
import com.example.model.AppSettings
import com.example.model.Language
import com.example.util.AppStrings
import com.example.util.NumberFormatter
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CalculatorUiState(
    val expression: String = "",
    val previewResult: String = "",
    val finalResult: String = "",
    val errorMessageKey: String? = null,
    val isScientificExpanded: Boolean = false,
    val isDegMode: Boolean = true,
    val activeTab: com.example.model.NavTab = com.example.model.NavTab.CALCULATOR
)

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CalculatorRepository
    init {
        val db = AppDatabase.getDatabase(application)
        repository = CalculatorRepository(db.historyDao(), db.favoriteDao())
    }

    val historyList: StateFlow<List<CalculationHistory>> = repository.allHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoritesList: StateFlow<List<FavoriteCalculation>> = repository.allFavorites
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    // --- Calculator Engine Actions ---

    fun onInput(char: String, settings: AppSettings) {
        val current = _uiState.value.expression
        val newExpr = when (char) {
            "+", "−", "×", "÷", "%", "^", "mod" -> {
                if (current.isEmpty()) {
                    if (char == "−") "-" else ""
                } else {
                    val lastChar = current.last()
                    if (lastChar in "+−×÷%^") {
                        current.dropLast(1) + char
                    } else {
                        current + char
                    }
                }
            }
            "." -> {
                // avoid duplicate dot in current number token
                val lastToken = current.split(Regex("[+−×÷%^() ]")).lastOrNull() ?: ""
                if (lastToken.contains(".")) current else "$current."
            }
            "±" -> {
                toggleSign(current)
            }
            "()" -> {
                insertParenthesis(current)
            }
            "sin", "cos", "tan", "sinh", "cosh", "tanh", "asin", "acos", "atan", "log", "ln", "√", "∛", "abs" -> {
                "$current$char("
            }
            "sqr" -> "$current^2"
            "cube" -> "$current^3"
            "inv" -> if (current.isEmpty()) "1/(" else "1/($current)"
            "fact" -> "$current!"
            "π" -> "${current}π"
            "e" -> "${current}e"
            "γ" -> "${current}γ"
            else -> current + char
        }

        _uiState.update {
            it.copy(
                expression = newExpr,
                errorMessageKey = null
            )
        }
        updatePreview(newExpr, settings)
    }

    private fun toggleSign(expr: String): String {
        if (expr.isEmpty()) return "-"
        val tokens = expr.split(Regex("(?<=[+−×÷%^(])|(?=[+−×÷%^)])"))
        if (tokens.isEmpty()) return "-"
        val last = tokens.last()
        return if (last.startsWith("-")) {
            expr.dropLast(last.length) + last.substring(1)
        } else if (last.startsWith("(-")) {
            expr.dropLast(last.length) + last.substring(2)
        } else {
            expr.dropLast(last.length) + "(-" + last + ")"
        }
    }

    private fun insertParenthesis(expr: String): String {
        val openCount = expr.count { it == '(' }
        val closeCount = expr.count { it == ')' }
        val lastChar = expr.lastOrNull()

        return if (openCount > closeCount && lastChar != null && (lastChar.isDigit() || lastChar == ')' || lastChar == 'π' || lastChar == 'e')) {
            "$expr)"
        } else {
            if (lastChar != null && (lastChar.isDigit() || lastChar == ')')) {
                "$expr×("
            } else {
                "$expr("
            }
        }
    }

    fun onBackspace(settings: AppSettings) {
        val current = _uiState.value.expression
        if (current.isNotEmpty()) {
            // Check if trailing is function name like "sin("
            val funcs = listOf("sinh(", "cosh(", "tanh(", "asin(", "acos(", "atan(", "sqrt(", "cbrt(", "sin(", "cos(", "tan(", "log(", "ln(", "abs(")
            var newExpr = current
            var matched = false
            for (f in funcs) {
                if (current.endsWith(f)) {
                    newExpr = current.dropLast(f.length)
                    matched = true
                    break
                }
            }
            if (!matched) {
                newExpr = current.dropLast(1)
            }

            _uiState.update { it.copy(expression = newExpr, errorMessageKey = null) }
            updatePreview(newExpr, settings)
        }
    }

    fun onClear() {
        _uiState.update {
            it.copy(
                expression = "",
                previewResult = "",
                finalResult = "",
                errorMessageKey = null
            )
        }
    }

    fun onEquals(settings: AppSettings) {
        val expr = _uiState.value.expression.trim()
        if (expr.isEmpty()) return

        val angleMode = if (_uiState.value.isDegMode) AngleMode.DEG else AngleMode.RAD
        val eval = CalculatorEngine.evaluate(
            expr,
            angleMode = angleMode,
            precision = settings.decimalPrecision,
            useThousandsSeparator = settings.useThousandsSeparator
        )

        when (eval) {
            is EvalResult.Success -> {
                val res = eval.formatted
                _uiState.update {
                    it.copy(
                        finalResult = res,
                        previewResult = "",
                        errorMessageKey = null
                    )
                }
                // Save to Room History
                viewModelScope.launch {
                    val calcType = if (_uiState.value.isScientificExpanded) CalculationType.SCIENTIFIC else CalculationType.BASIC
                    repository.addHistory(expr, res, calcType)
                }
            }
            is EvalResult.Error -> {
                _uiState.update {
                    it.copy(
                        errorMessageKey = eval.messageKey,
                        finalResult = ""
                    )
                }
            }
        }
    }

    private fun updatePreview(expr: String, settings: AppSettings) {
        if (expr.isBlank() || expr.length < 2) {
            _uiState.update { it.copy(previewResult = "") }
            return
        }
        val angleMode = if (_uiState.value.isDegMode) AngleMode.DEG else AngleMode.RAD
        val eval = CalculatorEngine.evaluate(
            expr,
            angleMode = angleMode,
            precision = settings.decimalPrecision,
            useThousandsSeparator = settings.useThousandsSeparator
        )
        if (eval is EvalResult.Success) {
            _uiState.update { it.copy(previewResult = eval.formatted) }
        } else {
            _uiState.update { it.copy(previewResult = "") }
        }
    }

    fun toggleScientificKeypad() {
        _uiState.update { it.copy(isScientificExpanded = !it.isScientificExpanded) }
    }

    fun toggleDegRad() {
        _uiState.update { it.copy(isDegMode = !it.isDegMode) }
    }

    fun setNavTab(tab: com.example.model.NavTab) {
        _uiState.update { it.copy(activeTab = tab) }
    }

    fun loadExpression(expression: String, result: String = "") {
        _uiState.update {
            it.copy(
                expression = expression,
                finalResult = result,
                previewResult = "",
                errorMessageKey = null,
                activeTab = com.example.model.NavTab.CALCULATOR
            )
        }
    }

    // --- History & Favorites ---

    fun deleteHistory(history: CalculationHistory) {
        viewModelScope.launch { repository.deleteHistory(history) }
    }

    fun clearAllHistory() {
        viewModelScope.launch { repository.clearHistory() }
    }

    fun addToFavorites(title: String, expression: String, result: String, note: String = "", type: CalculationType = CalculationType.BASIC) {
        viewModelScope.launch {
            repository.addFavorite(title, expression, result, note, type)
        }
    }

    fun updateFavorite(favorite: FavoriteCalculation) {
        viewModelScope.launch { repository.updateFavorite(favorite) }
    }

    fun deleteFavorite(favorite: FavoriteCalculation) {
        viewModelScope.launch { repository.deleteFavorite(favorite) }
    }

    // --- Copy & Share Helpers ---

    fun copyToClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Calculation", text)
        clipboard.setPrimaryClip(clip)
    }

    fun shareCalculation(context: Context, expression: String, result: String) {
        val text = "$expression = $result\n(Pro Calculator)"
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }
}
