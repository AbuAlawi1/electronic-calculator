package com.example.model

enum class Language(val code: String, val displayName: String, val nativeName: String) {
    ARABIC("ar", "Arabic", "العربية"),
    ENGLISH("en", "English", "English")
}

enum class ThemeMode(val titleEn: String, val titleAr: String) {
    SYSTEM("System Default", "تلقائي حسب النظام"),
    DARK("Dark Mode", "الوضع الداكن"),
    LIGHT("Light Mode", "الوضع الفاتح")
}

enum class ThemeAccent(
    val titleEn: String,
    val titleAr: String,
    val primaryHex: Long,
    val secondaryHex: Long
) {
    SAPPHIRE("Sapphire Blue", "أزرق ياقوتي", 0xFF0284C7, 0xFF38BDF8),
    EMERALD("Emerald Green", "أخضر زمردي", 0xFF059669, 0xFF34D399),
    AMBER("Sunset Amber", "كهرماني مشرق", 0xFFD97706, 0xFFFBBF24),
    PURPLE("Royal Purple", "بنفسجي ملكي", 0xFF7C3AED, 0xFFA78BFA),
    ROSE("Crimson Rose", "وردي قرمزي", 0xFFE11D48, 0xFFFB7185),
    CYAN("Cyan Teal", "تركوازي بحري", 0xFF0D9488, 0xFF2DD4BF)
}

enum class ButtonShapeStyle(val titleEn: String, val titleAr: String, val cornerRadiusDp: Int) {
    SQUIRCLE("Squircle", "شبه مربع منحني", 18),
    PILL("Pill / Rounded", "كبسولة دائرية", 32),
    CORNERED("Soft Square", "مربع ناعم", 12)
}

enum class AngleMode {
    DEG,
    RAD
}

enum class NavTab {
    CALCULATOR,
    CONVERTER,
    FINANCE,
    MATH,
    HISTORY,
    SETTINGS
}

data class AppSettings(
    val language: Language = Language.ARABIC,
    val themeMode: ThemeMode = ThemeMode.DARK,
    val accent: ThemeAccent = ThemeAccent.SAPPHIRE,
    val buttonShape: ButtonShapeStyle = ButtonShapeStyle.SQUIRCLE,
    val hapticFeedback: Boolean = true,
    val soundFeedback: Boolean = false,
    val decimalPrecision: Int = 8,
    val useThousandsSeparator: Boolean = true,
    val angleMode: AngleMode = AngleMode.DEG
)
