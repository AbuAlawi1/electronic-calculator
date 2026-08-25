package com.example.util

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object NumberFormatter {
    fun formatResult(
        value: Double,
        precision: Int = 8,
        useThousandsSeparator: Boolean = true
    ): String {
        if (value.isNaN()) return "NaN"
        if (value.isInfinite()) return if (value > 0) "∞" else "-∞"

        // If it's effectively an integer within safe long bounds
        if (value % 1.0 == 0.0 && Math.abs(value) < 1e15) {
            val longVal = value.toLong()
            return if (useThousandsSeparator) {
                val symbols = DecimalFormatSymbols(Locale.US)
                val formatter = DecimalFormat("#,###", symbols)
                formatter.format(longVal)
            } else {
                longVal.toString()
            }
        }

        // Scientific notation for very large or very small non-zero numbers
        val absVal = Math.abs(value)
        if ((absVal >= 1e12 || (absVal < 1e-6 && absVal > 0))) {
            val formatStr = "0." + "#".repeat(Math.min(precision, 6)) + "E0"
            val df = DecimalFormat(formatStr, DecimalFormatSymbols(Locale.US))
            return df.format(value)
        }

        return try {
            val bd = BigDecimal.valueOf(value)
                .setScale(precision, RoundingMode.HALF_UP)
                .stripTrailingZeros()
            val plain = bd.toPlainString()

            if (useThousandsSeparator) {
                val parts = plain.split(".")
                val intPart = parts[0].toLongOrNull()
                if (intPart != null) {
                    val symbols = DecimalFormatSymbols(Locale.US)
                    val formatter = DecimalFormat("#,###", symbols)
                    val formattedInt = formatter.format(intPart)
                    if (parts.size > 1) "$formattedInt.${parts[1]}" else formattedInt
                } else {
                    plain
                }
            } else {
                plain
            }
        } catch (e: Exception) {
            value.toString()
        }
    }

    fun cleanNumber(value: Double): Double {
        // Fix standard floating point precision artifacts like 0.1 + 0.2 = 0.30000000000000004
        if (value.isNaN() || value.isInfinite()) return value
        val bd = BigDecimal.valueOf(value).setScale(12, RoundingMode.HALF_UP)
        return bd.toDouble()
    }
}
