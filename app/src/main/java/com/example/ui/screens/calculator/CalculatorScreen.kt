package com.example.ui.screens.calculator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.AppSettings
import com.example.model.Language
import com.example.ui.components.CalcButton
import com.example.ui.components.CalcButtonType
import com.example.ui.components.DisplayScreen
import com.example.ui.viewmodel.CalculatorUiState

@Composable
fun CalculatorScreen(
    uiState: CalculatorUiState,
    settings: AppSettings,
    onInput: (String) -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit,
    onEquals: () -> Unit,
    onCopy: (String) -> Unit,
    onShare: (String, String) -> Unit,
    onSaveFavorite: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("calculator_screen"),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Expression & Result Display Screen
        DisplayScreen(
            expression = uiState.expression,
            previewResult = uiState.previewResult,
            finalResult = uiState.finalResult,
            errorMessageKey = uiState.errorMessageKey,
            language = settings.language,
            onCopy = onCopy,
            onShare = onShare,
            onSaveFavorite = onSaveFavorite,
            modifier = Modifier.weight(1f, fill = false)
        )

        // Keypad Container
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Scientific Functions Panel (Collapsible/Expandable)
            AnimatedVisibility(
                visible = uiState.isScientificExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                ScientificKeypad(
                    onInput = onInput,
                    settings = settings
                )
            }

            // Standard Basic Keypad
            BasicKeypad(
                onInput = onInput,
                onBackspace = onBackspace,
                onClear = onClear,
                onEquals = onEquals,
                settings = settings
            )
        }
    }
}

@Composable
fun ScientificKeypad(
    onInput: (String) -> Unit,
    settings: AppSettings,
    modifier: Modifier = Modifier
) {
    val scientificButtons = listOf(
        listOf("sin", "cos", "tan", "log", "ln"),
        listOf("sinh", "cosh", "tanh", "asin", "acos"),
        listOf("atan", "√", "∛", "sqr", "cube"),
        listOf("power", "inv", "abs", "fact", "mod"),
        listOf("π", "e", "γ", "(", ")")
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        scientificButtons.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                row.forEach { btn ->
                    val displayLabel = when (btn) {
                        "power" -> "^"
                        "sqr" -> "x²"
                        "cube" -> "x³"
                        "inv" -> "1/x"
                        "abs" -> "|x|"
                        "fact" -> "x!"
                        else -> btn
                    }
                    CalcButton(
                        text = displayLabel,
                        onClick = { onInput(btn) },
                        type = CalcButtonType.SCIENTIFIC,
                        buttonShape = settings.buttonShape,
                        hapticEnabled = settings.hapticFeedback,
                        soundEnabled = settings.soundFeedback,
                        fontSize = 15,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BasicKeypad(
    onInput: (String) -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit,
    onEquals: () -> Unit,
    settings: AppSettings,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Row 1: AC, (), %, ÷
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalcButton(
                text = "AC",
                onClick = onClear,
                type = CalcButtonType.ACTION_SECONDARY,
                buttonShape = settings.buttonShape,
                hapticEnabled = settings.hapticFeedback,
                soundEnabled = settings.soundFeedback,
                fontSize = 20,
                modifier = Modifier.weight(1f).height(60.dp),
                testTag = "btn_clear"
            )
            CalcButton(
                text = "( )",
                onClick = { onInput("()") },
                type = CalcButtonType.OPERATOR,
                buttonShape = settings.buttonShape,
                hapticEnabled = settings.hapticFeedback,
                soundEnabled = settings.soundFeedback,
                fontSize = 20,
                modifier = Modifier.weight(1f).height(60.dp),
                testTag = "btn_parentheses"
            )
            CalcButton(
                text = "%",
                onClick = { onInput("%") },
                type = CalcButtonType.OPERATOR,
                buttonShape = settings.buttonShape,
                hapticEnabled = settings.hapticFeedback,
                soundEnabled = settings.soundFeedback,
                fontSize = 22,
                modifier = Modifier.weight(1f).height(60.dp),
                testTag = "btn_percent"
            )
            CalcButton(
                text = "÷",
                onClick = { onInput("÷") },
                type = CalcButtonType.OPERATOR,
                buttonShape = settings.buttonShape,
                hapticEnabled = settings.hapticFeedback,
                soundEnabled = settings.soundFeedback,
                fontSize = 26,
                modifier = Modifier.weight(1f).height(60.dp),
                testTag = "btn_divide"
            )
        }

        // Row 2: 7, 8, 9, ×
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalcButton(
                text = "7",
                onClick = { onInput("7") },
                type = CalcButtonType.NUMBER,
                buttonShape = settings.buttonShape,
                hapticEnabled = settings.hapticFeedback,
                soundEnabled = settings.soundFeedback,
                fontSize = 24,
                modifier = Modifier.weight(1f).height(60.dp),
                testTag = "btn_7"
            )
            CalcButton(
                text = "8",
                onClick = { onInput("8") },
                type = CalcButtonType.NUMBER,
                buttonShape = settings.buttonShape,
                hapticEnabled = settings.hapticFeedback,
                soundEnabled = settings.soundFeedback,
                fontSize = 24,
                modifier = Modifier.weight(1f).height(60.dp),
                testTag = "btn_8"
            )
            CalcButton(
                text = "9",
                onClick = { onInput("9") },
                type = CalcButtonType.NUMBER,
                buttonShape = settings.buttonShape,
                hapticEnabled = settings.hapticFeedback,
                soundEnabled = settings.soundFeedback,
                fontSize = 24,
                modifier = Modifier.weight(1f).height(60.dp),
                testTag = "btn_9"
            )
            CalcButton(
                text = "×",
                onClick = { onInput("×") },
                type = CalcButtonType.OPERATOR,
                buttonShape = settings.buttonShape,
                hapticEnabled = settings.hapticFeedback,
                soundEnabled = settings.soundFeedback,
                fontSize = 26,
                modifier = Modifier.weight(1f).height(60.dp),
                testTag = "btn_multiply"
            )
        }

        // Row 3: 4, 5, 6, −
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalcButton(
                text = "4",
                onClick = { onInput("4") },
                type = CalcButtonType.NUMBER,
                buttonShape = settings.buttonShape,
                hapticEnabled = settings.hapticFeedback,
                soundEnabled = settings.soundFeedback,
                fontSize = 24,
                modifier = Modifier.weight(1f).height(60.dp),
                testTag = "btn_4"
            )
            CalcButton(
                text = "5",
                onClick = { onInput("5") },
                type = CalcButtonType.NUMBER,
                buttonShape = settings.buttonShape,
                hapticEnabled = settings.hapticFeedback,
                soundEnabled = settings.soundFeedback,
                fontSize = 24,
                modifier = Modifier.weight(1f).height(60.dp),
                testTag = "btn_5"
            )
            CalcButton(
                text = "6",
                onClick = { onInput("6") },
                type = CalcButtonType.NUMBER,
                buttonShape = settings.buttonShape,
                hapticEnabled = settings.hapticFeedback,
                soundEnabled = settings.soundFeedback,
                fontSize = 24,
                modifier = Modifier.weight(1f).height(60.dp),
                testTag = "btn_6"
            )
            CalcButton(
                text = "−",
                onClick = { onInput("−") },
                type = CalcButtonType.OPERATOR,
                buttonShape = settings.buttonShape,
                hapticEnabled = settings.hapticFeedback,
                soundEnabled = settings.soundFeedback,
                fontSize = 26,
                modifier = Modifier.weight(1f).height(60.dp),
                testTag = "btn_minus"
            )
        }

        // Row 4: 1, 2, 3, +
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalcButton(
                text = "1",
                onClick = { onInput("1") },
                type = CalcButtonType.NUMBER,
                buttonShape = settings.buttonShape,
                hapticEnabled = settings.hapticFeedback,
                soundEnabled = settings.soundFeedback,
                fontSize = 24,
                modifier = Modifier.weight(1f).height(60.dp),
                testTag = "btn_1"
            )
            CalcButton(
                text = "2",
                onClick = { onInput("2") },
                type = CalcButtonType.NUMBER,
                buttonShape = settings.buttonShape,
                hapticEnabled = settings.hapticFeedback,
                soundEnabled = settings.soundFeedback,
                fontSize = 24,
                modifier = Modifier.weight(1f).height(60.dp),
                testTag = "btn_2"
            )
            CalcButton(
                text = "3",
                onClick = { onInput("3") },
                type = CalcButtonType.NUMBER,
                buttonShape = settings.buttonShape,
                hapticEnabled = settings.hapticFeedback,
                soundEnabled = settings.soundFeedback,
                fontSize = 24,
                modifier = Modifier.weight(1f).height(60.dp),
                testTag = "btn_3"
            )
            CalcButton(
                text = "+",
                onClick = { onInput("+") },
                type = CalcButtonType.OPERATOR,
                buttonShape = settings.buttonShape,
                hapticEnabled = settings.hapticFeedback,
                soundEnabled = settings.soundFeedback,
                fontSize = 26,
                modifier = Modifier.weight(1f).height(60.dp),
                testTag = "btn_plus"
            )
        }

        // Row 5: ±, 0, ., ⌫, =
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CalcButton(
                text = "±",
                onClick = { onInput("±") },
                type = CalcButtonType.NUMBER,
                buttonShape = settings.buttonShape,
                hapticEnabled = settings.hapticFeedback,
                soundEnabled = settings.soundFeedback,
                fontSize = 20,
                modifier = Modifier.weight(1f).height(60.dp),
                testTag = "btn_plus_minus"
            )
            CalcButton(
                text = "0",
                onClick = { onInput("0") },
                type = CalcButtonType.NUMBER,
                buttonShape = settings.buttonShape,
                hapticEnabled = settings.hapticFeedback,
                soundEnabled = settings.soundFeedback,
                fontSize = 24,
                modifier = Modifier.weight(1f).height(60.dp),
                testTag = "btn_0"
            )
            CalcButton(
                text = ".",
                onClick = { onInput(".") },
                type = CalcButtonType.NUMBER,
                buttonShape = settings.buttonShape,
                hapticEnabled = settings.hapticFeedback,
                soundEnabled = settings.soundFeedback,
                fontSize = 26,
                modifier = Modifier.weight(1f).height(60.dp),
                testTag = "btn_dot"
            )
            CalcButton(
                text = "⌫",
                onClick = onBackspace,
                type = CalcButtonType.OPERATOR,
                buttonShape = settings.buttonShape,
                hapticEnabled = settings.hapticFeedback,
                soundEnabled = settings.soundFeedback,
                fontSize = 20,
                modifier = Modifier.weight(1f).height(60.dp),
                testTag = "btn_backspace"
            )
            CalcButton(
                text = "=",
                onClick = onEquals,
                type = CalcButtonType.ACTION_PRIMARY,
                buttonShape = settings.buttonShape,
                hapticEnabled = settings.hapticFeedback,
                soundEnabled = settings.soundFeedback,
                fontSize = 28,
                modifier = Modifier.weight(1.2f).height(60.dp),
                testTag = "btn_equals"
            )
        }
    }
}
