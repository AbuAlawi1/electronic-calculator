package com.example

import com.example.engine.CalculatorEngine
import com.example.engine.EvalResult
import com.example.engine.MathToolsEngine
import com.example.engine.UnitCategory
import com.example.engine.UnitConverterEngine
import com.example.model.AngleMode
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testBasicArithmetic() {
        val res = CalculatorEngine.evaluate("2+3*4")
        assertTrue(res is EvalResult.Success)
        assertEquals(14.0, (res as EvalResult.Success).value, 1e-6)
    }

    @Test
    fun testParenthesesAndPrecedence() {
        val res = CalculatorEngine.evaluate("(2+3)*4")
        assertTrue(res is EvalResult.Success)
        assertEquals(20.0, (res as EvalResult.Success).value, 1e-6)
    }

    @Test
    fun testScientificFunctions() {
        val sin30 = CalculatorEngine.evaluate("sin(30)", angleMode = AngleMode.DEG)
        assertTrue(sin30 is EvalResult.Success)
        assertEquals(0.5, (sin30 as EvalResult.Success).value, 1e-6)

        val sqrt16 = CalculatorEngine.evaluate("sqrt(16)")
        assertTrue(sqrt16 is EvalResult.Success)
        assertEquals(4.0, (sqrt16 as EvalResult.Success).value, 1e-6)
    }

    @Test
    fun testDivisionByZero() {
        val res = CalculatorEngine.evaluate("10/0")
        assertTrue(res is EvalResult.Error)
    }

    @Test
    fun testUnitConverter() {
        val lengthUnits = UnitConverterEngine.getUnits(UnitCategory.LENGTH)
        val m = lengthUnits.first { it.id == "m" }
        val km = lengthUnits.first { it.id == "km" }
        val converted = UnitConverterEngine.convert(5000.0, m, km)
        assertEquals(5.0, converted, 1e-6)
    }

    @Test
    fun testPrimeAndLcmGcd() {
        assertTrue(MathToolsEngine.isPrime(97))
        assertFalse(MathToolsEngine.isPrime(100))
        assertEquals(12L, MathToolsEngine.gcd(24, 36))
        assertEquals(72L, MathToolsEngine.lcm(24, 36))
    }
}
