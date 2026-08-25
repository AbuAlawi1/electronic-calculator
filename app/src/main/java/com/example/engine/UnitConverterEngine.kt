package com.example.engine

import com.example.model.Language

enum class UnitCategory(val id: String, val titleKey: String) {
    LENGTH("length", "length"),
    MASS("mass", "mass"),
    AREA("area", "area"),
    VOLUME("volume", "volume"),
    SPEED("speed", "speed"),
    TIME("time", "time"),
    TEMPERATURE("temperature", "temperature"),
    STORAGE("storage", "storage"),
    ANGLE("angle", "angle"),
    CURRENCY("currency", "currency")
}

data class UnitItem(
    val id: String,
    val nameEn: String,
    val nameAr: String,
    val symbol: String,
    // Conversion factor to base unit (e.g. meter, gram, etc.)
    val toBase: (Double) -> Double,
    val fromBase: (Double) -> Double
)

object UnitConverterEngine {

    val categories = UnitCategory.values().toList()

    fun getUnits(category: UnitCategory): List<UnitItem> = when (category) {
        UnitCategory.LENGTH -> listOf(
            UnitItem("m", "Meter", "متر", "m", { it }, { it }),
            UnitItem("km", "Kilometer", "كيلومتر", "km", { it * 1000.0 }, { it / 1000.0 }),
            UnitItem("cm", "Centimeter", "سنتيمتر", "cm", { it / 100.0 }, { it * 100.0 }),
            UnitItem("mm", "Millimeter", "مليمتر", "mm", { it / 1000.0 }, { it * 1000.0 }),
            UnitItem("in", "Inch", "بوصة", "in", { it * 0.0254 }, { it / 0.0254 }),
            UnitItem("ft", "Foot", "قدم", "ft", { it * 0.3048 }, { it / 0.3048 }),
            UnitItem("yd", "Yard", "ياردا", "yd", { it * 0.9144 }, { it / 0.9144 }),
            UnitItem("mi", "Mile", "ميل", "mi", { it * 1609.344 }, { it / 1609.344 }),
            UnitItem("nmi", "Nautical Mile", "ميل بحري", "nmi", { it * 1852.0 }, { it / 1852.0 })
        )

        UnitCategory.MASS -> listOf(
            UnitItem("kg", "Kilogram", "كيلوغرام", "kg", { it * 1000.0 }, { it / 1000.0 }),
            UnitItem("g", "Gram", "غرام", "g", { it }, { it }),
            UnitItem("mg", "Milligram", "مليغرام", "mg", { it / 1000.0 }, { it * 1000.0 }),
            UnitItem("lb", "Pound", "رطل", "lb", { it * 453.59237 }, { it / 453.59237 }),
            UnitItem("oz", "Ounce", "أونصة", "oz", { it * 28.349523 }, { it / 28.349523 }),
            UnitItem("t", "Metric Ton", "طن متري", "t", { it * 1000000.0 }, { it / 1000000.0 }),
            UnitItem("st", "Stone", "ستون", "st", { it * 6350.29318 }, { it / 6350.29318 })
        )

        UnitCategory.AREA -> listOf(
            UnitItem("sqm", "Square Meter", "متر مربع", "m²", { it }, { it }),
            UnitItem("sqkm", "Square Kilometer", "كيلومتر مربع", "km²", { it * 1_000_000.0 }, { it / 1_000_000.0 }),
            UnitItem("sqcm", "Square Centimeter", "سنتيمتر مربع", "cm²", { it / 10000.0 }, { it * 10000.0 }),
            UnitItem("sqmm", "Square Millimeter", "مليمتر مربع", "mm²", { it / 1_000_000.0 }, { it * 1_000_000.0 }),
            UnitItem("ha", "Hectare", "هكتار", "ha", { it * 10000.0 }, { it / 10000.0 }),
            UnitItem("ac", "Acre", "فدان / إيكر", "ac", { it * 4046.85642 }, { it / 4046.85642 }),
            UnitItem("sqft", "Square Foot", "قدم مربع", "ft²", { it * 0.092903 }, { it / 0.092903 }),
            UnitItem("sqyd", "Square Yard", "ياردا مربعة", "yd²", { it * 0.836127 }, { it / 0.836127 }),
            UnitItem("sqmi", "Square Mile", "ميل مربع", "mi²", { it * 2589988.11 }, { it / 2589988.11 })
        )

        UnitCategory.VOLUME -> listOf(
            UnitItem("l", "Liter", "لتر", "L", { it }, { it }),
            UnitItem("ml", "Milliliter", "مليلتر", "mL", { it / 1000.0 }, { it * 1000.0 }),
            UnitItem("m3", "Cubic Meter", "متر مكعب", "m³", { it * 1000.0 }, { it / 1000.0 }),
            UnitItem("gal_us", "Gallon (US)", "غالون (أمريكي)", "gal", { it * 3.78541 }, { it / 3.78541 }),
            UnitItem("gal_uk", "Gallon (UK)", "غالون (بريطاني)", "imp gal", { it * 4.54609 }, { it / 4.54609 }),
            UnitItem("floz", "Fluid Ounce (US)", "أونصة سائلة", "fl oz", { it * 0.0295735 }, { it / 0.0295735 }),
            UnitItem("cup", "Cup (US)", "كوب", "cup", { it * 0.24 }, { it / 0.24 }),
            UnitItem("pt", "Pint (US)", "باينت", "pt", { it * 0.473176 }, { it / 0.473176 })
        )

        UnitCategory.SPEED -> listOf(
            UnitItem("mps", "Meter/Second", "متر/ثانية", "m/s", { it }, { it }),
            UnitItem("kmh", "Kilometer/Hour", "كيلومتر/ساعة", "km/h", { it / 3.6 }, { it * 3.6 }),
            UnitItem("mph", "Mile/Hour", "ميل/ساعة", "mph", { it * 0.44704 }, { it / 0.44704 }),
            UnitItem("knot", "Knot", "عقدة بحرية", "kn", { it * 0.514444 }, { it / 0.514444 }),
            UnitItem("fps", "Foot/Second", "قدم/ثانية", "ft/s", { it * 0.3048 }, { it / 0.3048 })
        )

        UnitCategory.TIME -> listOf(
            UnitItem("ms", "Millisecond", "ملي ثانية", "ms", { it / 1000.0 }, { it * 1000.0 }),
            UnitItem("s", "Second", "ثانية", "s", { it }, { it }),
            UnitItem("min", "Minute", "دقيقة", "min", { it * 60.0 }, { it / 60.0 }),
            UnitItem("h", "Hour", "ساعة", "h", { it * 3600.0 }, { it / 3600.0 }),
            UnitItem("d", "Day", "يوم", "d", { it * 86400.0 }, { it / 86400.0 }),
            UnitItem("wk", "Week", "أسبوع", "wk", { it * 604800.0 }, { it / 604800.0 }),
            UnitItem("mo", "Month (30d)", "شهر", "mo", { it * 2592000.0 }, { it / 2592000.0 }),
            UnitItem("yr", "Year (365d)", "سنة", "yr", { it * 31536000.0 }, { it / 31536000.0 })
        )

        UnitCategory.TEMPERATURE -> listOf(
            UnitItem("c", "Celsius", "درجة مئوية", "°C", { it }, { it }),
            UnitItem("f", "Fahrenheit", "فهرنهايت", "°F", { (it - 32.0) * 5.0 / 9.0 }, { (it * 9.0 / 5.0) + 32.0 }),
            UnitItem("k", "Kelvin", "كلفن", "K", { it - 273.15 }, { it + 273.15 })
        )

        UnitCategory.STORAGE -> listOf(
            UnitItem("b", "Byte", "بايت", "B", { it }, { it }),
            UnitItem("kb", "Kilobyte (KB)", "كيلوبايت", "KB", { it * 1024.0 }, { it / 1024.0 }),
            UnitItem("mb", "Megabyte (MB)", "ميغابايت", "MB", { it * 1024.0 * 1024.0 }, { it / (1024.0 * 1024.0) }),
            UnitItem("gb", "Gigabyte (GB)", "غيغابايت", "GB", { it * 1024.0 * 1024.0 * 1024.0 }, { it / (1024.0 * 1024.0 * 1024.0) }),
            UnitItem("tb", "Terabyte (TB)", "تيرابايت", "TB", { it * Math.pow(1024.0, 4.0) }, { it / Math.pow(1024.0, 4.0) }),
            UnitItem("pb", "Petabyte (PB)", "بيتابايت", "PB", { it * Math.pow(1024.0, 5.0) }, { it / Math.pow(1024.0, 5.0) })
        )

        UnitCategory.ANGLE -> listOf(
            UnitItem("deg", "Degree", "درجة", "°", { it }, { it }),
            UnitItem("rad", "Radian", "راديان", "rad", { Math.toDegrees(it) }, { Math.toRadians(it) }),
            UnitItem("grad", "Gradian", "جراديان", "grad", { it * 0.9 }, { it / 0.9 })
        )

        UnitCategory.CURRENCY -> listOf(
            // USD as base
            UnitItem("usd", "US Dollar", "دولار أمريكي", "$ USD", { it }, { it }),
            UnitItem("eur", "Euro", "يورو", "€ EUR", { it * 1.08 }, { it / 1.08 }),
            UnitItem("gbp", "British Pound", "جنيه إسترليني", "£ GBP", { it * 1.28 }, { it / 1.28 }),
            UnitItem("sar", "Saudi Riyal", "ريال سعودي", "SAR", { it * 0.2666 }, { it / 0.2666 }),
            UnitItem("aed", "UAE Dirham", "درهم إماراتي", "AED", { it * 0.2723 }, { it / 0.2723 }),
            UnitItem("kwd", "Kuwaiti Dinar", "دينار كويتي", "KWD", { it * 3.25 }, { it / 3.25 }),
            UnitItem("qar", "Qatari Riyal", "ريال قطري", "QAR", { it * 0.2747 }, { it / 0.2747 }),
            UnitItem("egp", "Egyptian Pound", "جنيه مصري", "EGP", { it * 0.0205 }, { it / 0.0205 }),
            UnitItem("jpy", "Japanese Yen", "ين ياباني", "¥ JPY", { it * 0.0065 }, { it / 0.0065 }),
            UnitItem("cad", "Canadian Dollar", "دولار كندي", "CA$", { it * 0.73 }, { it / 0.73 }),
            UnitItem("aud", "Australian Dollar", "دولار أسترالي", "AU$", { it * 0.65 }, { it / 0.65 }),
            UnitItem("try", "Turkish Lira", "ليرة تركية", "TRY", { it * 0.029 }, { it / 0.029 })
        )
    }

    fun convert(value: Double, fromUnit: UnitItem, toUnit: UnitItem): Double {
        val baseVal = fromUnit.toBase(value)
        return toUnit.fromBase(baseVal)
    }
}
