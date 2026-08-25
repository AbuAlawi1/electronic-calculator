package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("pro_calc_settings", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    private fun loadSettings(): AppSettings {
        val langCode = prefs.getString("language", Language.ARABIC.code) ?: Language.ARABIC.code
        val lang = if (langCode == Language.ENGLISH.code) Language.ENGLISH else Language.ARABIC

        val themeName = prefs.getString("theme_mode", ThemeMode.DARK.name) ?: ThemeMode.DARK.name
        val themeMode = runCatching { ThemeMode.valueOf(themeName) }.getOrDefault(ThemeMode.DARK)

        val accentName = prefs.getString("accent", ThemeAccent.SAPPHIRE.name) ?: ThemeAccent.SAPPHIRE.name
        val accent = runCatching { ThemeAccent.valueOf(accentName) }.getOrDefault(ThemeAccent.SAPPHIRE)

        val shapeName = prefs.getString("button_shape", ButtonShapeStyle.SQUIRCLE.name) ?: ButtonShapeStyle.SQUIRCLE.name
        val shape = runCatching { ButtonShapeStyle.valueOf(shapeName) }.getOrDefault(ButtonShapeStyle.SQUIRCLE)

        val haptic = prefs.getBoolean("haptic", true)
        val sound = prefs.getBoolean("sound", false)
        val precision = prefs.getInt("precision", 8)
        val thousands = prefs.getBoolean("thousands", true)

        val angleName = prefs.getString("angle_mode", AngleMode.DEG.name) ?: AngleMode.DEG.name
        val angle = runCatching { AngleMode.valueOf(angleName) }.getOrDefault(AngleMode.DEG)

        return AppSettings(
            language = lang,
            themeMode = themeMode,
            accent = accent,
            buttonShape = shape,
            hapticFeedback = haptic,
            soundFeedback = sound,
            decimalPrecision = precision,
            useThousandsSeparator = thousands,
            angleMode = angle
        )
    }

    fun setLanguage(language: Language) {
        prefs.edit().putString("language", language.code).apply()
        _settings.update { it.copy(language = language) }
    }

    fun setThemeMode(mode: ThemeMode) {
        prefs.edit().putString("theme_mode", mode.name).apply()
        _settings.update { it.copy(themeMode = mode) }
    }

    fun setAccent(accent: ThemeAccent) {
        prefs.edit().putString("accent", accent.name).apply()
        _settings.update { it.copy(accent = accent) }
    }

    fun setButtonShape(shape: ButtonShapeStyle) {
        prefs.edit().putString("button_shape", shape.name).apply()
        _settings.update { it.copy(buttonShape = shape) }
    }

    fun setHapticFeedback(enabled: Boolean) {
        prefs.edit().putBoolean("haptic", enabled).apply()
        _settings.update { it.copy(hapticFeedback = enabled) }
    }

    fun setSoundFeedback(enabled: Boolean) {
        prefs.edit().putBoolean("sound", enabled).apply()
        _settings.update { it.copy(soundFeedback = enabled) }
    }

    fun setDecimalPrecision(precision: Int) {
        prefs.edit().putInt("precision", precision).apply()
        _settings.update { it.copy(decimalPrecision = precision) }
    }

    fun setThousandsSeparator(enabled: Boolean) {
        prefs.edit().putBoolean("thousands", enabled).apply()
        _settings.update { it.copy(useThousandsSeparator = enabled) }
    }

    fun setAngleMode(mode: AngleMode) {
        prefs.edit().putString("angle_mode", mode.name).apply()
        _settings.update { it.copy(angleMode = mode) }
    }

    fun resetSettings() {
        prefs.edit().clear().apply()
        _settings.value = AppSettings()
    }
}
