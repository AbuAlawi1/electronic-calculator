package com.example

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.Language
import com.example.model.NavTab
import com.example.model.ThemeMode
import com.example.ui.components.AppBottomNavigationBar
import com.example.ui.components.AppHeader
import com.example.ui.components.AppNavigationRail
import com.example.ui.screens.calculator.CalculatorScreen
import com.example.ui.screens.converter.ConverterScreen
import com.example.ui.screens.finance.FinanceScreen
import com.example.ui.screens.history.HistoryScreen
import com.example.ui.screens.math.MathToolsScreen
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.theme.ProCalculatorTheme
import com.example.ui.viewmodel.CalculatorViewModel
import com.example.ui.viewmodel.SettingsViewModel
import com.example.util.AppStrings

class MainActivity : ComponentActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels()
    private val calculatorViewModel: CalculatorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val settings by settingsViewModel.settings.collectAsStateWithLifecycle()
            val calcUiState by calculatorViewModel.uiState.collectAsStateWithLifecycle()
            val historyList by calculatorViewModel.historyList.collectAsStateWithLifecycle()
            val favoritesList by calculatorViewModel.favoritesList.collectAsStateWithLifecycle()

            val layoutDirection = if (settings.language == Language.ARABIC) {
                LayoutDirection.Rtl
            } else {
                LayoutDirection.Ltr
            }

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                ProCalculatorTheme(
                    themeMode = settings.themeMode,
                    accent = settings.accent
                ) {
                    val context = LocalContext.current

                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxSize()
                            .onKeyEvent { keyEvent ->
                                if (keyEvent.type == KeyEventType.KeyDown) {
                                    handleHardwareKey(keyEvent.nativeKeyEvent.keyCode, keyEvent.nativeKeyEvent.unicodeChar)
                                } else false
                            }
                            .focusable()
                    ) {
                        val isExpandedScreen = maxWidth >= 600.dp

                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            containerColor = MaterialTheme.colorScheme.background,
                            contentWindowInsets = WindowInsets.safeDrawing,
                            bottomBar = {
                                if (!isExpandedScreen) {
                                    AppBottomNavigationBar(
                                        currentTab = calcUiState.activeTab,
                                        onTabSelected = { calculatorViewModel.setNavTab(it) },
                                        language = settings.language
                                    )
                                }
                            }
                        ) { innerPadding ->
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                            ) {
                                if (isExpandedScreen) {
                                    AppNavigationRail(
                                        currentTab = calcUiState.activeTab,
                                        onTabSelected = { calculatorViewModel.setNavTab(it) },
                                        language = settings.language
                                    )
                                }

                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                ) {
                                    // Top App Bar / Header
                                    val currentTitle = when (calcUiState.activeTab) {
                                        NavTab.CALCULATOR -> AppStrings.get("calculator", settings.language)
                                        NavTab.CONVERTER -> AppStrings.get("converter", settings.language)
                                        NavTab.FINANCE -> AppStrings.get("finance", settings.language)
                                        NavTab.MATH -> AppStrings.get("math", settings.language)
                                        NavTab.HISTORY -> AppStrings.get("history", settings.language)
                                        NavTab.SETTINGS -> AppStrings.get("settings", settings.language)
                                    }

                                    AppHeader(
                                        currentTitle = currentTitle,
                                        language = settings.language,
                                        isScientificExpanded = calcUiState.isScientificExpanded,
                                        isDegMode = calcUiState.isDegMode,
                                        themeMode = settings.themeMode,
                                        onToggleScientific = { calculatorViewModel.toggleScientificKeypad() },
                                        onToggleAngleMode = { calculatorViewModel.toggleDegRad() },
                                        onToggleTheme = {
                                            val nextMode = if (settings.themeMode == ThemeMode.DARK) ThemeMode.LIGHT else ThemeMode.DARK
                                            settingsViewModel.setThemeMode(nextMode)
                                        },
                                        onToggleLanguage = {
                                            val nextLang = if (settings.language == Language.ARABIC) Language.ENGLISH else Language.ARABIC
                                            settingsViewModel.setLanguage(nextLang)
                                        },
                                        onOpenSettings = { calculatorViewModel.setNavTab(NavTab.SETTINGS) },
                                        showCalcControls = calcUiState.activeTab == NavTab.CALCULATOR
                                    )

                                    // Main Active Screen View
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxWidth()
                                    ) {
                                        when (calcUiState.activeTab) {
                                            NavTab.CALCULATOR -> {
                                                CalculatorScreen(
                                                    uiState = calcUiState,
                                                    settings = settings,
                                                    onInput = { calculatorViewModel.onInput(it, settings) },
                                                    onBackspace = { calculatorViewModel.onBackspace(settings) },
                                                    onClear = { calculatorViewModel.onClear() },
                                                    onEquals = { calculatorViewModel.onEquals(settings) },
                                                    onCopy = { calculatorViewModel.copyToClipboard(context, it) },
                                                    onShare = { expr, res -> calculatorViewModel.shareCalculation(context, expr, res) },
                                                    onSaveFavorite = { title, expr, res ->
                                                        calculatorViewModel.addToFavorites(title, expr, res)
                                                    }
                                                )
                                            }

                                            NavTab.CONVERTER -> {
                                                ConverterScreen(settings = settings)
                                            }

                                            NavTab.FINANCE -> {
                                                FinanceScreen(settings = settings)
                                            }

                                            NavTab.MATH -> {
                                                MathToolsScreen(settings = settings)
                                            }

                                            NavTab.HISTORY -> {
                                                HistoryScreen(
                                                    historyList = historyList,
                                                    favoritesList = favoritesList,
                                                    settings = settings,
                                                    onReuse = { expr, res -> calculatorViewModel.loadExpression(expr, res) },
                                                    onDeleteHistory = { calculatorViewModel.deleteHistory(it) },
                                                    onClearAllHistory = { calculatorViewModel.clearAllHistory() },
                                                    onAddFavorite = { title, expr, res, note ->
                                                        calculatorViewModel.addToFavorites(title, expr, res, note)
                                                    },
                                                    onUpdateFavorite = { calculatorViewModel.updateFavorite(it) },
                                                    onDeleteFavorite = { calculatorViewModel.deleteFavorite(it) },
                                                    onCopy = { calculatorViewModel.copyToClipboard(context, it) },
                                                    onShare = { expr, res -> calculatorViewModel.shareCalculation(context, expr, res) }
                                                )
                                            }

                                            NavTab.SETTINGS -> {
                                                SettingsScreen(
                                                    settings = settings,
                                                    onSetLanguage = { settingsViewModel.setLanguage(it) },
                                                    onSetThemeMode = { settingsViewModel.setThemeMode(it) },
                                                    onSetAccent = { settingsViewModel.setAccent(it) },
                                                    onSetButtonShape = { settingsViewModel.setButtonShape(it) },
                                                    onSetHaptic = { settingsViewModel.setHapticFeedback(it) },
                                                    onSetSound = { settingsViewModel.setSoundFeedback(it) },
                                                    onSetDecimalPrecision = { settingsViewModel.setDecimalPrecision(it) },
                                                    onSetThousandsSeparator = { settingsViewModel.setThousandsSeparator(it) },
                                                    onResetSettings = { settingsViewModel.resetSettings() }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun handleHardwareKey(keyCode: Int, unicodeChar: Int): Boolean {
        val settings = settingsViewModel.settings.value
        return when (keyCode) {
            KeyEvent.KEYCODE_0, KeyEvent.KEYCODE_NUMPAD_0 -> { calculatorViewModel.onInput("0", settings); true }
            KeyEvent.KEYCODE_1, KeyEvent.KEYCODE_NUMPAD_1 -> { calculatorViewModel.onInput("1", settings); true }
            KeyEvent.KEYCODE_2, KeyEvent.KEYCODE_NUMPAD_2 -> { calculatorViewModel.onInput("2", settings); true }
            KeyEvent.KEYCODE_3, KeyEvent.KEYCODE_NUMPAD_3 -> { calculatorViewModel.onInput("3", settings); true }
            KeyEvent.KEYCODE_4, KeyEvent.KEYCODE_NUMPAD_4 -> { calculatorViewModel.onInput("4", settings); true }
            KeyEvent.KEYCODE_5, KeyEvent.KEYCODE_NUMPAD_5 -> { calculatorViewModel.onInput("5", settings); true }
            KeyEvent.KEYCODE_6, KeyEvent.KEYCODE_NUMPAD_6 -> { calculatorViewModel.onInput("6", settings); true }
            KeyEvent.KEYCODE_7, KeyEvent.KEYCODE_NUMPAD_7 -> { calculatorViewModel.onInput("7", settings); true }
            KeyEvent.KEYCODE_8, KeyEvent.KEYCODE_NUMPAD_8 -> { calculatorViewModel.onInput("8", settings); true }
            KeyEvent.KEYCODE_9, KeyEvent.KEYCODE_NUMPAD_9 -> { calculatorViewModel.onInput("9", settings); true }
            KeyEvent.KEYCODE_PLUS, KeyEvent.KEYCODE_NUMPAD_ADD -> { calculatorViewModel.onInput("+", settings); true }
            KeyEvent.KEYCODE_MINUS, KeyEvent.KEYCODE_NUMPAD_SUBTRACT -> { calculatorViewModel.onInput("−", settings); true }
            KeyEvent.KEYCODE_STAR, KeyEvent.KEYCODE_NUMPAD_MULTIPLY -> { calculatorViewModel.onInput("×", settings); true }
            KeyEvent.KEYCODE_SLASH, KeyEvent.KEYCODE_NUMPAD_DIVIDE -> { calculatorViewModel.onInput("÷", settings); true }
            KeyEvent.KEYCODE_PERIOD, KeyEvent.KEYCODE_NUMPAD_DOT -> { calculatorViewModel.onInput(".", settings); true }
            KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_NUMPAD_ENTER, KeyEvent.KEYCODE_EQUALS -> { calculatorViewModel.onEquals(settings); true }
            KeyEvent.KEYCODE_DEL -> { calculatorViewModel.onBackspace(settings); true }
            KeyEvent.KEYCODE_ESCAPE -> { calculatorViewModel.onClear(); true }
            else -> {
                if (unicodeChar > 0) {
                    val char = unicodeChar.toChar()
                    if (char in "+-*%/^()") {
                        val mapped = when (char) {
                            '*' -> "×"
                            '/' -> "÷"
                            '-' -> "−"
                            else -> char.toString()
                        }
                        calculatorViewModel.onInput(mapped, settings)
                        true
                    } else false
                } else false
            }
        }
    }
}
