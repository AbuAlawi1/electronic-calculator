package com.example.ui.screens.math

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.MathToolsEngine
import com.example.model.AppSettings
import com.example.model.Language
import com.example.ui.screens.finance.FinanceResultCard
import com.example.util.AppStrings
import com.example.util.NumberFormatter

enum class MathToolTab(val key: String) {
    STATISTICS("statistics"),
    RATIO("ratio_proportion"),
    LCM_GCD("lcm_gcd"),
    PRIMES("prime_numbers"),
    PERM_COMB("perm_comb"),
    TRIGONOMETRY("trigonometry"),
    GEOMETRY("geometry"),
    FRACTIONS("fractions")
}

@Composable
fun MathToolsScreen(
    settings: AppSettings,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(MathToolTab.STATISTICS) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("math_tools_screen")
    ) {
        // Horizontal Scrollable Math Tool Chips
        val scrollState = rememberScrollState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MathToolTab.values().forEach { tab ->
                val isSelected = tab == activeTab
                FilterChip(
                    selected = isSelected,
                    onClick = { activeTab = tab },
                    label = {
                        Text(
                            text = AppStrings.get(tab.key, settings.language),
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.testTag("chip_math_${tab.name.lowercase()}")
                )
            }
        }

        // Active Math View Body
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            when (activeTab) {
                MathToolTab.STATISTICS -> StatisticsView(settings)
                MathToolTab.RATIO -> RatioView(settings)
                MathToolTab.LCM_GCD -> LcmGcdView(settings)
                MathToolTab.PRIMES -> PrimesView(settings)
                MathToolTab.PERM_COMB -> PermCombView(settings)
                MathToolTab.TRIGONOMETRY -> TrigonometryView(settings)
                MathToolTab.GEOMETRY -> GeometryView(settings)
                MathToolTab.FRACTIONS -> FractionsView(settings)
            }
        }
    }
}

@Composable
fun StatisticsView(settings: AppSettings) {
    var rawInput by remember { mutableStateOf("12, 18.5, 4, 9, 24, 18.5, 30") }

    val numbers = remember(rawInput) {
        rawInput.split(Regex("[,\\s]+"))
            .mapNotNull { it.toDoubleOrNull() }
    }

    val stats = remember(numbers) {
        MathToolsEngine.calculateStats(numbers)
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        OutlinedTextField(
            value = rawInput,
            onValueChange = { rawInput = it },
            label = { Text(AppStrings.get("statistics", settings.language)) },
            placeholder = { Text(AppStrings.get("enter_numbers_hint", settings.language)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )

        if (stats != null) {
            FinanceResultCard(
                title = AppStrings.get("statistics", settings.language),
                items = listOf(
                    AppStrings.get("count", settings.language) to stats.count.toString(),
                    AppStrings.get("sum", settings.language) to NumberFormatter.formatResult(stats.sum, settings.decimalPrecision, settings.useThousandsSeparator),
                    AppStrings.get("mean", settings.language) to NumberFormatter.formatResult(stats.mean, settings.decimalPrecision, settings.useThousandsSeparator),
                    AppStrings.get("median", settings.language) to NumberFormatter.formatResult(stats.median, settings.decimalPrecision, settings.useThousandsSeparator),
                    AppStrings.get("mode", settings.language) to if (stats.mode.isEmpty()) "-" else stats.mode.joinToString(", ") { NumberFormatter.formatResult(it, 2) },
                    AppStrings.get("min", settings.language) to NumberFormatter.formatResult(stats.min, settings.decimalPrecision, settings.useThousandsSeparator),
                    AppStrings.get("max", settings.language) to NumberFormatter.formatResult(stats.max, settings.decimalPrecision, settings.useThousandsSeparator),
                    AppStrings.get("variance", settings.language) to NumberFormatter.formatResult(stats.variance, settings.decimalPrecision, settings.useThousandsSeparator),
                    AppStrings.get("std_dev", settings.language) to NumberFormatter.formatResult(stats.stdDev, settings.decimalPrecision, settings.useThousandsSeparator)
                ),
                highlightIndex = 2
            )
        }
    }
}

@Composable
fun RatioView(settings: AppSettings) {
    var aStr by remember { mutableStateOf("3") }
    var bStr by remember { mutableStateOf("4") }
    var cStr by remember { mutableStateOf("9") }
    var dStr by remember { mutableStateOf("") }

    val a = aStr.toDoubleOrNull()
    val b = bStr.toDoubleOrNull()
    val c = cStr.toDoubleOrNull()
    val d = dStr.toDoubleOrNull()

    val solved = remember(a, b, c, d) {
        MathToolsEngine.solveProportion(a, b, c, d)
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "${AppStrings.get("ratio_formula", settings.language)} - ${AppStrings.get("find_missing", settings.language)}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = aStr,
                onValueChange = { aStr = it },
                label = { Text("A") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp)
            )
            OutlinedTextField(
                value = bStr,
                onValueChange = { bStr = it },
                label = { Text("B") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = cStr,
                onValueChange = { cStr = it },
                label = { Text("C") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp)
            )
            OutlinedTextField(
                value = dStr,
                onValueChange = { dStr = it },
                label = { Text("X (D)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp)
            )
        }

        if (solved != null) {
            FinanceResultCard(
                title = AppStrings.get("ratio_proportion", settings.language),
                items = listOf(
                    AppStrings.get("find_missing", settings.language) to NumberFormatter.formatResult(solved, settings.decimalPrecision, settings.useThousandsSeparator)
                ),
                highlightIndex = 0
            )
        }
    }
}

@Composable
fun LcmGcdView(settings: AppSettings) {
    var rawInput by remember { mutableStateOf("24, 36, 60") }

    val numbers = remember(rawInput) {
        rawInput.split(Regex("[,\\s]+"))
            .mapNotNull { it.toLongOrNull() }
            .filter { it > 0 }
    }

    val lcmVal = remember(numbers) { if (numbers.isNotEmpty()) MathToolsEngine.lcmMultiple(numbers) else null }
    val gcdVal = remember(numbers) { if (numbers.isNotEmpty()) MathToolsEngine.gcdMultiple(numbers) else null }

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        OutlinedTextField(
            value = rawInput,
            onValueChange = { rawInput = it },
            label = { Text(AppStrings.get("lcm_gcd", settings.language)) },
            placeholder = { Text(AppStrings.get("enter_two_or_more", settings.language)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )

        if (lcmVal != null && gcdVal != null) {
            FinanceResultCard(
                title = AppStrings.get("lcm_gcd", settings.language),
                items = listOf(
                    AppStrings.get("lcm", settings.language) to lcmVal.toString(),
                    AppStrings.get("gcd", settings.language) to gcdVal.toString()
                ),
                highlightIndex = 0
            )
        }
    }
}

@Composable
fun PrimesView(settings: AppSettings) {
    var inputStr by remember { mutableStateOf("97") }
    val n = inputStr.toLongOrNull() ?: 0L

    val isPrime = remember(n) { MathToolsEngine.isPrime(n) }
    val nextP = remember(n) { MathToolsEngine.nextPrime(n) }
    val prevP = remember(n) { MathToolsEngine.prevPrime(n) }
    val factors = remember(n) { MathToolsEngine.getFactors(n) }
    val primeFactors = remember(n) { MathToolsEngine.primeFactorization(n) }

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        OutlinedTextField(
            value = inputStr,
            onValueChange = { inputStr = it },
            label = { Text(AppStrings.get("enter_integer", settings.language)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )

        val primeFactorStr = primeFactors.entries.joinToString(" × ") {
            if (it.value > 1) "${it.key}^${it.value}" else "${it.key}"
        }.ifEmpty { "-" }

        FinanceResultCard(
            title = "${AppStrings.get("prime_numbers", settings.language)} ($n)",
            items = listOf(
                AppStrings.get("is_prime", settings.language) to (if (isPrime) AppStrings.get("yes_prime", settings.language) else AppStrings.get("not_prime", settings.language)),
                AppStrings.get("prime_factors", settings.language) to primeFactorStr,
                AppStrings.get("next_prime", settings.language) to nextP.toString(),
                AppStrings.get("prev_prime", settings.language) to (prevP?.toString() ?: "-"),
                AppStrings.get("factors", settings.language) to factors.take(15).joinToString(", ") + (if (factors.size > 15) "..." else "")
            ),
            highlightIndex = 0
        )
    }
}

@Composable
fun PermCombView(settings: AppSettings) {
    var nStr by remember { mutableStateOf("10") }
    var rStr by remember { mutableStateOf("3") }

    val n = nStr.toLongOrNull() ?: 0L
    val r = rStr.toLongOrNull() ?: 0L

    val perm = remember(n, r) { MathToolsEngine.permutations(n, r) }
    val comb = remember(n, r) { MathToolsEngine.combinations(n, r) }

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = nStr,
                onValueChange = { nStr = it },
                label = { Text(AppStrings.get("total_n", settings.language)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp)
            )
            OutlinedTextField(
                value = rStr,
                onValueChange = { rStr = it },
                label = { Text(AppStrings.get("choose_r", settings.language)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp)
            )
        }

        FinanceResultCard(
            title = AppStrings.get("perm_comb", settings.language),
            items = listOf(
                AppStrings.get("permutations", settings.language) + " P($n, $r)" to perm.toString(),
                AppStrings.get("combinations", settings.language) + " C($n, $r)" to comb.toString()
            ),
            highlightIndex = 1
        )
    }
}

@Composable
fun TrigonometryView(settings: AppSettings) {
    var aStr by remember { mutableStateOf("3") }
    var bStr by remember { mutableStateOf("4") }
    var cStr by remember { mutableStateOf("") }

    val a = aStr.toDoubleOrNull()
    val b = bStr.toDoubleOrNull()
    val c = cStr.toDoubleOrNull()

    val res = remember(a, b, c) { MathToolsEngine.solveRightTriangleBySides(a, b, c) }

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = AppStrings.get("right_triangle", settings.language),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = aStr,
                onValueChange = { aStr = it },
                label = { Text(AppStrings.get("side_a", settings.language)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp)
            )
            OutlinedTextField(
                value = bStr,
                onValueChange = { bStr = it },
                label = { Text(AppStrings.get("side_b", settings.language)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp)
            )
            OutlinedTextField(
                value = cStr,
                onValueChange = { cStr = it },
                label = { Text(AppStrings.get("hypotenuse_c", settings.language)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp)
            )
        }

        if (res != null) {
            FinanceResultCard(
                title = AppStrings.get("trigonometry", settings.language),
                items = listOf(
                    AppStrings.get("side_a", settings.language) to NumberFormatter.formatResult(res.a, 2),
                    AppStrings.get("side_b", settings.language) to NumberFormatter.formatResult(res.b, 2),
                    AppStrings.get("hypotenuse_c", settings.language) to NumberFormatter.formatResult(res.c, 2),
                    AppStrings.get("angle_A", settings.language) to "${NumberFormatter.formatResult(res.angleADeg, 2)}°",
                    AppStrings.get("angle_B", settings.language) to "${NumberFormatter.formatResult(res.angleBDeg, 2)}°",
                    AppStrings.get("area", settings.language) to NumberFormatter.formatResult(res.area, 2),
                    AppStrings.get("perimeter", settings.language) to NumberFormatter.formatResult(res.perimeter, 2)
                ),
                highlightIndex = 2
            )
        }
    }
}

@Composable
fun GeometryView(settings: AppSettings) {
    val shapes = listOf("circle", "square", "rectangle", "cylinder", "sphere", "cube")
    var selectedShape by remember { mutableStateOf("circle") }

    var param1 by remember { mutableStateOf("5") } // radius / side / length
    var param2 by remember { mutableStateOf("10") } // height / width

    val v1 = param1.toDoubleOrNull() ?: 0.0
    val v2 = param2.toDoubleOrNull() ?: 0.0

    val res = remember(selectedShape, v1, v2) {
        when (selectedShape) {
            "circle" -> MathToolsEngine.calcCircle(v1)
            "square" -> MathToolsEngine.calcSquare(v1)
            "rectangle" -> MathToolsEngine.calcRectangle(v1, v2)
            "cylinder" -> MathToolsEngine.calcCylinder(v1, v2)
            "sphere" -> MathToolsEngine.calcSphere(v1)
            "cube" -> MathToolsEngine.calcCube(v1)
            else -> null
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        val scrollState = rememberScrollState()
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            shapes.forEach { sh ->
                FilterChip(
                    selected = selectedShape == sh,
                    onClick = { selectedShape = sh },
                    label = { Text(AppStrings.get(sh, settings.language)) }
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = param1,
                onValueChange = { param1 = it },
                label = { Text(if (selectedShape in listOf("circle", "cylinder", "sphere")) "Radius" else "Side / Length") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp)
            )
            if (selectedShape in listOf("rectangle", "cylinder")) {
                OutlinedTextField(
                    value = param2,
                    onValueChange = { param2 = it },
                    label = { Text(if (selectedShape == "rectangle") "Width" else "Height") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                )
            }
        }

        if (res != null) {
            val list = mutableListOf<Pair<String, String>>()
            if (res.area != null) list.add(AppStrings.get("area", settings.language) to NumberFormatter.formatResult(res.area, 2))
            if (res.perimeter != null) list.add(AppStrings.get("perimeter", settings.language) to NumberFormatter.formatResult(res.perimeter, 2))
            if (res.surfaceArea != null) list.add(AppStrings.get("surface_area", settings.language) to NumberFormatter.formatResult(res.surfaceArea, 2))
            if (res.volume != null) list.add(AppStrings.get("volume", settings.language) to NumberFormatter.formatResult(res.volume, 2))

            FinanceResultCard(
                title = AppStrings.get(selectedShape, settings.language),
                items = list,
                highlightIndex = 0
            )
        }
    }
}

@Composable
fun FractionsView(settings: AppSettings) {
    var num1Str by remember { mutableStateOf("3") }
    var den1Str by remember { mutableStateOf("4") }
    var op by remember { mutableStateOf("+") }
    var num2Str by remember { mutableStateOf("2") }
    var den2Str by remember { mutableStateOf("5") }

    val n1 = num1Str.toLongOrNull() ?: 1L
    val d1 = den1Str.toLongOrNull() ?: 1L
    val n2 = num2Str.toLongOrNull() ?: 1L
    val d2 = den2Str.toLongOrNull() ?: 1L

    val res = remember(n1, d1, op, n2, d2) {
        MathToolsEngine.calculateFractions(n1, d1, op, n2, d2)
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = AppStrings.get("fractions", settings.language),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Fraction 1
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                OutlinedTextField(
                    value = num1Str,
                    onValueChange = { num1Str = it },
                    label = { Text("Num 1") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(10.dp)
                )
                OutlinedTextField(
                    value = den1Str,
                    onValueChange = { den1Str = it },
                    label = { Text("Den 1") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(10.dp)
                )
            }

            // Op selector
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf("+", "-", "×", "÷").forEach { operator ->
                    FilterChip(
                        selected = op == operator,
                        onClick = { op = operator },
                        label = { Text(operator, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            // Fraction 2
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                OutlinedTextField(
                    value = num2Str,
                    onValueChange = { num2Str = it },
                    label = { Text("Num 2") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(10.dp)
                )
                OutlinedTextField(
                    value = den2Str,
                    onValueChange = { den2Str = it },
                    label = { Text("Den 2") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }

        if (res != null) {
            val mixedStr = if (res.wholeNumber != null && res.remainderNum != null) {
                "${res.wholeNumber} (${res.remainderNum}/${res.denominator})"
            } else if (res.wholeNumber != null) {
                "${res.wholeNumber}"
            } else {
                "${res.numerator}/${res.denominator}"
            }

            FinanceResultCard(
                title = AppStrings.get("fractions", settings.language),
                items = listOf(
                    AppStrings.get("simplified", settings.language) to "${res.numerator} / ${res.denominator}",
                    AppStrings.get("mixed_number", settings.language) to mixedStr,
                    AppStrings.get("decimal_value", settings.language) to NumberFormatter.formatResult(res.decimal, settings.decimalPrecision, settings.useThousandsSeparator)
                ),
                highlightIndex = 0
            )
        }
    }
}
