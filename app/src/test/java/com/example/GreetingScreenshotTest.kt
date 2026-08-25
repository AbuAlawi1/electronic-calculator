package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.model.AppSettings
import com.example.ui.screens.calculator.CalculatorScreen
import com.example.ui.theme.ProCalculatorTheme
import com.example.ui.viewmodel.CalculatorUiState
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [34])
class GreetingScreenshotTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun greeting_screenshot() {
        val settings = AppSettings()
        val uiState = CalculatorUiState(expression = "128 × 4", previewResult = "512")

        composeTestRule.setContent {
            ProCalculatorTheme {
                CalculatorScreen(
                    uiState = uiState,
                    settings = settings,
                    onInput = {},
                    onBackspace = {},
                    onClear = {},
                    onEquals = {},
                    onCopy = {},
                    onShare = { _, _ -> },
                    onSaveFavorite = { _, _, _ -> }
                )
            }
        }

        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
    }
}
