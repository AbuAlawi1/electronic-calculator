package com.example.engine

import kotlin.math.pow

object FinancialEngine {

    data class SimpleInterestResult(
        val principal: Double,
        val interest: Double,
        val totalAmount: Double
    )

    fun calculateSimpleInterest(principal: Double, ratePercent: Double, timeYears: Double): SimpleInterestResult {
        val interest = principal * (ratePercent / 100.0) * timeYears
        return SimpleInterestResult(
            principal = principal,
            interest = interest,
            totalAmount = principal + interest
        )
    }

    data class CompoundInterestResult(
        val principal: Double,
        val interest: Double,
        val totalAmount: Double
    )

    fun calculateCompoundInterest(
        principal: Double,
        ratePercent: Double,
        timeYears: Double,
        compoundingFreqPerYear: Int
    ): CompoundInterestResult {
        val r = ratePercent / 100.0
        val n = compoundingFreqPerYear.toDouble().coerceAtLeast(1.0)
        val total = principal * (1.0 + r / n).pow(n * timeYears)
        val interest = total - principal
        return CompoundInterestResult(
            principal = principal,
            interest = interest,
            totalAmount = total
        )
    }

    data class DiscountResult(
        val originalPrice: Double,
        val discountAmount: Double,
        val finalPrice: Double,
        val effectivePercent: Double
    )

    fun calculateDiscount(originalPrice: Double, discountPercent: Double, extraPercent: Double = 0.0): DiscountResult {
        val firstCut = originalPrice * (1.0 - discountPercent / 100.0)
        val finalPrice = firstCut * (1.0 - extraPercent / 100.0)
        val saved = originalPrice - finalPrice
        val effective = if (originalPrice > 0) (saved / originalPrice) * 100.0 else 0.0
        return DiscountResult(
            originalPrice = originalPrice,
            discountAmount = saved,
            finalPrice = finalPrice,
            effectivePercent = effective
        )
    }

    data class TaxResult(
        val netAmount: Double,
        val taxAmount: Double,
        val grossAmount: Double
    )

    fun calculateTax(amount: Double, taxRatePercent: Double, isTaxIncluded: Boolean): TaxResult {
        val r = taxRatePercent / 100.0
        return if (isTaxIncluded) {
            val net = amount / (1.0 + r)
            val tax = amount - net
            TaxResult(netAmount = net, taxAmount = tax, grossAmount = amount)
        } else {
            val tax = amount * r
            val gross = amount + tax
            TaxResult(netAmount = amount, taxAmount = tax, grossAmount = gross)
        }
    }

    data class PercentageChangeResult(
        val oldValue: Double,
        val newValue: Double,
        val difference: Double,
        val percentChange: Double,
        val isIncrease: Boolean
    )

    fun calculatePercentageChange(oldValue: Double, newValue: Double): PercentageChangeResult {
        val diff = newValue - oldValue
        val percent = if (oldValue != 0.0) (diff / Math.abs(oldValue)) * 100.0 else 0.0
        return PercentageChangeResult(
            oldValue = oldValue,
            newValue = newValue,
            difference = Math.abs(diff),
            percentChange = Math.abs(percent),
            isIncrease = diff >= 0
        )
    }

    data class EmiResult(
        val loanAmount: Double,
        val monthlyEmi: Double,
        val totalInterest: Double,
        val totalPayment: Double
    )

    fun calculateEmi(principal: Double, annualRatePercent: Double, tenureMonths: Int): EmiResult {
        if (principal <= 0 || tenureMonths <= 0) {
            return EmiResult(principal, 0.0, 0.0, principal)
        }
        if (annualRatePercent <= 0.0) {
            val monthly = principal / tenureMonths
            return EmiResult(principal, monthly, 0.0, principal)
        }
        val monthlyRate = (annualRatePercent / 100.0) / 12.0
        val n = tenureMonths.toDouble()
        val emi = (principal * monthlyRate * (1.0 + monthlyRate).pow(n)) /
                ((1.0 + monthlyRate).pow(n) - 1.0)
        val totalPayment = emi * n
        val totalInterest = totalPayment - principal

        return EmiResult(
            loanAmount = principal,
            monthlyEmi = emi,
            totalInterest = totalInterest,
            totalPayment = totalPayment
        )
    }
}
