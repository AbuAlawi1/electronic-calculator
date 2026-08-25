package com.example.ui.screens.finance

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
import com.example.engine.FinancialEngine
import com.example.model.AppSettings
import com.example.util.AppStrings
import com.example.util.NumberFormatter

enum class FinanceTab(val key: String) {
    SIMPLE_INTEREST("simple_interest"),
    COMPOUND_INTEREST("compound_interest"),
    DISCOUNT("discount"),
    TAX("tax"),
    PERCENT_CHANGE("percent_change"),
    LOAN_EMI("loan_emi")
}

@Composable
fun FinanceScreen(
    settings: AppSettings,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(FinanceTab.SIMPLE_INTEREST) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("finance_screen")
    ) {
        // Finance Category Chips
        val scrollState = rememberScrollState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FinanceTab.values().forEach { tab ->
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
                    modifier = Modifier.testTag("chip_finance_${tab.name.lowercase()}")
                )
            }
        }

        // Selected Tool Body
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            when (activeTab) {
                FinanceTab.SIMPLE_INTEREST -> SimpleInterestView(settings)
                FinanceTab.COMPOUND_INTEREST -> CompoundInterestView(settings)
                FinanceTab.DISCOUNT -> DiscountView(settings)
                FinanceTab.TAX -> TaxView(settings)
                FinanceTab.PERCENT_CHANGE -> PercentChangeView(settings)
                FinanceTab.LOAN_EMI -> LoanEmiView(settings)
            }
        }
    }
}

@Composable
fun SimpleInterestView(settings: AppSettings) {
    var principalStr by remember { mutableStateOf("10000") }
    var rateStr by remember { mutableStateOf("5") }
    var timeStr by remember { mutableStateOf("3") }

    val p = principalStr.toDoubleOrNull() ?: 0.0
    val r = rateStr.toDoubleOrNull() ?: 0.0
    val t = timeStr.toDoubleOrNull() ?: 0.0

    val res = remember(p, r, t) {
        FinancialEngine.calculateSimpleInterest(p, r, t)
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        OutlinedTextField(
            value = principalStr,
            onValueChange = { principalStr = it },
            label = { Text(AppStrings.get("principal", settings.language)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )
        OutlinedTextField(
            value = rateStr,
            onValueChange = { rateStr = it },
            label = { Text(AppStrings.get("interest_rate", settings.language)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )
        OutlinedTextField(
            value = timeStr,
            onValueChange = { timeStr = it },
            label = { Text(AppStrings.get("time_years", settings.language)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )

        FinanceResultCard(
            title = AppStrings.get("simple_interest", settings.language),
            items = listOf(
                AppStrings.get("principal", settings.language) to NumberFormatter.formatResult(res.principal, 2, settings.useThousandsSeparator),
                AppStrings.get("total_interest", settings.language) to NumberFormatter.formatResult(res.interest, 2, settings.useThousandsSeparator),
                AppStrings.get("total_payable", settings.language) to NumberFormatter.formatResult(res.totalAmount, 2, settings.useThousandsSeparator)
            ),
            highlightIndex = 2
        )
    }
}

@Composable
fun CompoundInterestView(settings: AppSettings) {
    var principalStr by remember { mutableStateOf("10000") }
    var rateStr by remember { mutableStateOf("6") }
    var timeStr by remember { mutableStateOf("5") }
    var freqStr by remember { mutableStateOf("12") } // monthly

    val p = principalStr.toDoubleOrNull() ?: 0.0
    val r = rateStr.toDoubleOrNull() ?: 0.0
    val t = timeStr.toDoubleOrNull() ?: 0.0
    val n = freqStr.toIntOrNull() ?: 12

    val res = remember(p, r, t, n) {
        FinancialEngine.calculateCompoundInterest(p, r, t, n)
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        OutlinedTextField(
            value = principalStr,
            onValueChange = { principalStr = it },
            label = { Text(AppStrings.get("principal", settings.language)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )
        OutlinedTextField(
            value = rateStr,
            onValueChange = { rateStr = it },
            label = { Text(AppStrings.get("interest_rate", settings.language)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )
        OutlinedTextField(
            value = timeStr,
            onValueChange = { timeStr = it },
            label = { Text(AppStrings.get("time_years", settings.language)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )
        OutlinedTextField(
            value = freqStr,
            onValueChange = { freqStr = it },
            label = { Text(AppStrings.get("compound_freq", settings.language)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )

        FinanceResultCard(
            title = AppStrings.get("compound_interest", settings.language),
            items = listOf(
                AppStrings.get("principal", settings.language) to NumberFormatter.formatResult(res.principal, 2, settings.useThousandsSeparator),
                AppStrings.get("total_interest", settings.language) to NumberFormatter.formatResult(res.interest, 2, settings.useThousandsSeparator),
                AppStrings.get("total_payable", settings.language) to NumberFormatter.formatResult(res.totalAmount, 2, settings.useThousandsSeparator)
            ),
            highlightIndex = 2
        )
    }
}

@Composable
fun DiscountView(settings: AppSettings) {
    var priceStr by remember { mutableStateOf("250") }
    var discountStr by remember { mutableStateOf("20") }
    var extraStr by remember { mutableStateOf("5") }

    val p = priceStr.toDoubleOrNull() ?: 0.0
    val d = discountStr.toDoubleOrNull() ?: 0.0
    val e = extraStr.toDoubleOrNull() ?: 0.0

    val res = remember(p, d, e) {
        FinancialEngine.calculateDiscount(p, d, e)
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        OutlinedTextField(
            value = priceStr,
            onValueChange = { priceStr = it },
            label = { Text(AppStrings.get("original_price", settings.language)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )
        OutlinedTextField(
            value = discountStr,
            onValueChange = { discountStr = it },
            label = { Text(AppStrings.get("discount_percent", settings.language)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )
        OutlinedTextField(
            value = extraStr,
            onValueChange = { extraStr = it },
            label = { Text(AppStrings.get("extra_discount", settings.language)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )

        FinanceResultCard(
            title = AppStrings.get("discount", settings.language),
            items = listOf(
                AppStrings.get("original_price", settings.language) to NumberFormatter.formatResult(res.originalPrice, 2, settings.useThousandsSeparator),
                AppStrings.get("you_save", settings.language) to "${NumberFormatter.formatResult(res.discountAmount, 2, settings.useThousandsSeparator)} (${NumberFormatter.formatResult(res.effectivePercent, 1)}%)",
                AppStrings.get("final_price", settings.language) to NumberFormatter.formatResult(res.finalPrice, 2, settings.useThousandsSeparator)
            ),
            highlightIndex = 2
        )
    }
}

@Composable
fun TaxView(settings: AppSettings) {
    var amountStr by remember { mutableStateOf("1000") }
    var taxRateStr by remember { mutableStateOf("15") }
    var isIncluded by remember { mutableStateOf(false) }

    val a = amountStr.toDoubleOrNull() ?: 0.0
    val r = taxRateStr.toDoubleOrNull() ?: 0.0

    val res = remember(a, r, isIncluded) {
        FinancialEngine.calculateTax(a, r, isIncluded)
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        OutlinedTextField(
            value = amountStr,
            onValueChange = { amountStr = it },
            label = { Text(AppStrings.get("amount", settings.language)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )
        OutlinedTextField(
            value = taxRateStr,
            onValueChange = { taxRateStr = it },
            label = { Text(AppStrings.get("tax_rate", settings.language)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FilterChip(
                selected = !isIncluded,
                onClick = { isIncluded = false },
                label = { Text(AppStrings.get("tax_excluded", settings.language)) },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = isIncluded,
                onClick = { isIncluded = true },
                label = { Text(AppStrings.get("tax_included", settings.language)) },
                modifier = Modifier.weight(1f)
            )
        }

        FinanceResultCard(
            title = AppStrings.get("tax", settings.language),
            items = listOf(
                AppStrings.get("net_amount", settings.language) to NumberFormatter.formatResult(res.netAmount, 2, settings.useThousandsSeparator),
                AppStrings.get("tax_amount", settings.language) to NumberFormatter.formatResult(res.taxAmount, 2, settings.useThousandsSeparator),
                AppStrings.get("gross_amount", settings.language) to NumberFormatter.formatResult(res.grossAmount, 2, settings.useThousandsSeparator)
            ),
            highlightIndex = 2
        )
    }
}

@Composable
fun PercentChangeView(settings: AppSettings) {
    var oldStr by remember { mutableStateOf("500") }
    var newStr by remember { mutableStateOf("650") }

    val o = oldStr.toDoubleOrNull() ?: 0.0
    val n = newStr.toDoubleOrNull() ?: 0.0

    val res = remember(o, n) {
        FinancialEngine.calculatePercentageChange(o, n)
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        OutlinedTextField(
            value = oldStr,
            onValueChange = { oldStr = it },
            label = { Text(AppStrings.get("old_value", settings.language)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )
        OutlinedTextField(
            value = newStr,
            onValueChange = { newStr = it },
            label = { Text(AppStrings.get("new_value", settings.language)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )

        FinanceResultCard(
            title = AppStrings.get("percent_change", settings.language),
            items = listOf(
                AppStrings.get("difference", settings.language) to NumberFormatter.formatResult(res.difference, 2, settings.useThousandsSeparator),
                AppStrings.get("percent_change", settings.language) to "${if (res.isIncrease) "+" else "-"}${NumberFormatter.formatResult(res.percentChange, 2)}% (${if (res.isIncrease) AppStrings.get("increase", settings.language) else AppStrings.get("decrease", settings.language)})"
            ),
            highlightIndex = 1
        )
    }
}

@Composable
fun LoanEmiView(settings: AppSettings) {
    var loanStr by remember { mutableStateOf("100000") }
    var rateStr by remember { mutableStateOf("7.5") }
    var tenureStr by remember { mutableStateOf("36") }

    val p = loanStr.toDoubleOrNull() ?: 0.0
    val r = rateStr.toDoubleOrNull() ?: 0.0
    val m = tenureStr.toIntOrNull() ?: 36

    val res = remember(p, r, m) {
        FinancialEngine.calculateEmi(p, r, m)
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        OutlinedTextField(
            value = loanStr,
            onValueChange = { loanStr = it },
            label = { Text(AppStrings.get("loan_amount", settings.language)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )
        OutlinedTextField(
            value = rateStr,
            onValueChange = { rateStr = it },
            label = { Text(AppStrings.get("interest_rate", settings.language)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )
        OutlinedTextField(
            value = tenureStr,
            onValueChange = { tenureStr = it },
            label = { Text(AppStrings.get("time_months", settings.language)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )

        FinanceResultCard(
            title = AppStrings.get("loan_emi", settings.language),
            items = listOf(
                AppStrings.get("monthly_payment", settings.language) to NumberFormatter.formatResult(res.monthlyEmi, 2, settings.useThousandsSeparator),
                AppStrings.get("total_interest", settings.language) to NumberFormatter.formatResult(res.totalInterest, 2, settings.useThousandsSeparator),
                AppStrings.get("total_payable", settings.language) to NumberFormatter.formatResult(res.totalPayment, 2, settings.useThousandsSeparator)
            ),
            highlightIndex = 0
        )
    }
}

@Composable
fun FinanceResultCard(
    title: String,
    items: List<Pair<String, String>>,
    highlightIndex: Int = -1
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            items.forEachIndexed { index, pair ->
                val isHigh = index == highlightIndex
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = pair.first,
                        style = if (isHigh) MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold) else MaterialTheme.typography.bodyMedium,
                        color = if (isHigh) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = pair.second,
                        style = if (isHigh) MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold) else MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = if (isHigh) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
